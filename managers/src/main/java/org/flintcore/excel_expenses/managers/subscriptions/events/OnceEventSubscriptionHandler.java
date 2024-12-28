package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.managers.subscriptions.OnceRunnableSubscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscriptionHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@Primary
public final class OnceEventSubscriptionHandler
        extends RunnableSubscriptionHolder<EventType<WorkerStateEvent>>
        implements IOnceEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    @Override
    public Subscription handleOnce(EventType<WorkerStateEvent> key, Runnable value) {
        this.initHolder();

        var listHolder = this.eventHandler.computeIfAbsent(key, this::buildHolder);

        OnceRunnableSubscription subs = OnceRunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(listHolder::remove)
                .build();

        listHolder.add(subs);

        return subs;
    }

    @Override
    public Subscription handleOnce(List<EventType<WorkerStateEvent>> keys, Runnable runnable) {
        if (keys.isEmpty()) return DEFAULT_VALUE;

        return keys.stream().map(k -> this.handleOnce(k, runnable))
                .reduce(Subscription::and)
                .orElse(DEFAULT_VALUE);
    }
}
