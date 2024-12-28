package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscriptionHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * FX Service / Task event handler.
 */
@Component
@Scope("prototype")
@Primary
public final class EventSubscriptionHandler
        extends RunnableSubscriptionHolder<EventType<WorkerStateEvent>>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    @Override
    public Subscription handle(EventType<WorkerStateEvent> key, Runnable value) {
        this.initHolder();

        var listHolder = this.eventHandler.computeIfAbsent(key, this::buildHolder);

        RunnableSubscription subs = RunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(listHolder::remove)
                .build();

        listHolder.add(subs);

        return subs;
    }

    @Override
    public Subscription handle(List<EventType<WorkerStateEvent>> keys, Runnable value) {
        if (keys.isEmpty()) return DEFAULT_VALUE;

        return keys.stream().map(k -> this.handle(k, value))
                .reduce(Subscription::and)
                .orElse(DEFAULT_VALUE);
    }
}
