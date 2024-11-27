package org.flintcore.excel_expenses.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;
import org.springframework.context.annotation.Lazy;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service to handle CRUD operations in background FX threads.
 */
@Log4j2
public abstract class FxService<T> extends Service<T> {
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ShutdownFXApplication shutDownHolder;
    protected FXRunnableEventHandler eventHandler;

    protected ObservableSet<T> dataSetList;

    protected AtomicBoolean requiresRequest;

    // For logging and messaging

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
    }

    public FxService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder,
            FXRunnableEventHandler eventHandler
    ) {
        this.subscriptionManager = subscriptionManager;
        this.shutDownHolder = shutDownHolder;
        this.eventHandler = eventHandler;
    }

    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;

        EventHandler<WorkerStateEvent> eventListenerHandler = event -> {
            EventType<? extends Event> eventType = event.getEventType();

            if (eventType.getSuperType() != WorkerStateEvent.ANY) return;
            log.info(eventType.toString());
            this.eventHandler.accept((EventType<WorkerStateEvent>) eventType);
        };

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }


    @PreDestroy
    protected void onClose() {
        Platform.runLater(this.subscriptionManager::close);
    }
}
