package org.flintcore.excel_expenses.managers.services;

import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IOnceEventSubscriptionHandler;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public abstract class ScheduledFxService<T> extends ScheduledService<T>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable>,
        IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    @Lazy
    protected final ShutdownFXApplication shutDownHolder;
    @Lazy
    protected GeneralEventSubscriptionHandler eventHandler;

    protected AtomicBoolean requiresRequest;

    public ScheduledFxService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutDownHolder
    ) {
        this.eventHandler = eventHandler;
        this.shutDownHolder = shutDownHolder;
        this.requiresRequest = new AtomicBoolean(true);
    }

    /**
     * Is in delayed state, force to init service
     */
    public void forceStart() throws RuntimeException {
        if (isRunning()) return;
        this.restart();
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

    @PreDestroy
    protected void onClose() throws IOException {
        Platform.runLater(this.shutDownHolder::close);
    }
}
