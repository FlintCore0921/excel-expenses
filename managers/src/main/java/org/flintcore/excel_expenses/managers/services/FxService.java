package org.flintcore.excel_expenses.managers.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IOnceEventSubscriptionHandler;
import org.flintcore.utilities.iterations.EventIterationUtils;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service to handle CRUD operations in background FX threads.
 */
@Log4j2
public abstract class FxService<T> extends Service<T>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable>,
        IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {
    @Lazy
    protected final ShutdownFXApplication shutDownHolder;
    protected GeneralEventSubscriptionHandler eventHandler;

    protected AtomicBoolean requiresRequest;

    // For logging and messaging

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
    }

    public FxService(
            GeneralEventSubscriptionHandler subscriptionManager,
            ShutdownFXApplication shutDownHolder
    ) {
        this.eventHandler = subscriptionManager;
        this.shutDownHolder = shutDownHolder;

        setupSubscriptionsHandler();
    }

    @Override
    public Subscription handle(EventType<WorkerStateEvent> keys, Runnable value) {
        return this.eventHandler.handle(keys, value);
    }

    @Override
    public Subscription handle(List<EventType<WorkerStateEvent>> keys, Runnable value) {
        return this.eventHandler.handle(keys, value);
    }

    @Override
    public Subscription handleOnce(EventType<WorkerStateEvent> key, Runnable runnable) {
        return this.eventHandler.handleOnce(key, runnable);
    }

    @Override
    public Subscription handleOnce(List<EventType<WorkerStateEvent>> keys, Runnable runnable) {
        return this.eventHandler.handleOnce(keys, runnable);
    }

    @Override
    public void accept(EventType<WorkerStateEvent> key) {
        this.eventHandler.accept(key);
    }


    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;
        EventIterationUtils.appendListenerTo(this, this.eventHandler);
    }


    @PreDestroy
    protected void onClose() {
        Platform.runLater(this.shutDownHolder::close);
    }
}
