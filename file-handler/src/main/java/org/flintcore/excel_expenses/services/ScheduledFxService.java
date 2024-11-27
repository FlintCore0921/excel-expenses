package org.flintcore.excel_expenses.services;

import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.ScheduledService;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;
import org.springframework.context.annotation.Lazy;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public abstract class ScheduledFxService<T> extends ScheduledService<T> {
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ShutdownFXApplication shutDownHolder;

    protected FXRunnableEventHandler eventHandler;

    protected ObservableSet<T> dataSetList;

    protected AtomicBoolean requiresRequest;

    public ScheduledFxService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder,
            FXRunnableEventHandler eventHandler
    ) {
        this.subscriptionManager = subscriptionManager;
        this.shutDownHolder = shutDownHolder;
        this.requiresRequest = new AtomicBoolean(true);
        this.eventHandler = eventHandler;
    }

    /** Is in delayed state, force to init service*/
    public void forceStart() throws RuntimeException {
        if(isRunning()) return;
        this.restart();
    }

    /**
     * Init on load tasks when triggers.
     */
    protected void setupOnLoadListeners() {
    }

    /**
     * Trigger to listen when store requests
     */
    protected void setupOnReadListeners() {
    }

    protected void setupShutdownActions() {
    }

    protected void initObservableList() {
        if (Objects.nonNull(this.dataSetList)) return;

        if (!Platform.isFxApplicationThread())
            Platform.runLater(this::initFields);
        else this.initFields();

        setupOnLoadListeners();
        setupOnReadListeners();
    }

    @SuppressWarnings("unchecked")
    private void initFields() {
        this.dataSetList = FXCollections.observableSet();
    }

    @PreDestroy
    protected void onClose() {
        this.shutDownHolder.close();
        this.subscriptionManager.close();
    }
}
