package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@AllArgsConstructor
public class GeneralEventSubscriptionHandler
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable>,
        IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    @Lazy
    private final IEventSubscriptionHandler<WorkerStateEvent, Runnable> eventHandler;
    @Lazy
    private final IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> onceEventHandler;

    @Override
    public Subscription handle(EventType<WorkerStateEvent> key, Runnable value) {
        return this.eventHandler.handle(key, value);
    }

    @Override
    public Subscription handle(List<EventType<WorkerStateEvent>> keys, Runnable value) {
        return this.eventHandler.handle(keys, value);
    }

    @Override
    public Subscription handleOnce(EventType<WorkerStateEvent> key, Runnable runnable) {
        return this.onceEventHandler.handleOnce(key, runnable);
    }

    @Override
    public Subscription handleOnce(List<EventType<WorkerStateEvent>> keys, Runnable runnable) {
        return this.onceEventHandler.handleOnce(keys, runnable);
    }

    @Override
    public void accept(EventType<WorkerStateEvent> key) {
        this.eventHandler.accept(key);
        this.onceEventHandler.accept(key);
    }
}
