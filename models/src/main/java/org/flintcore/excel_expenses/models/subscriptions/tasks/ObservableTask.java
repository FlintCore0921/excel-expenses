package org.flintcore.excel_expenses.models.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public abstract class ObservableTask<T> extends Task<T>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    private Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.listenSubscription(type, action);
    }

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> getEventListenerHolder() {
        NullableUtils.executeIsNull(this.events, () -> this.events = new ConcurrentHashMap<>());
        return events;
    }

    protected Subscription listenSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        try {
            Set<Runnable> subscriptions = getEventListenerHolder()
                    .computeIfAbsent(type, __ -> Collections.synchronizedSet(new HashSet<>()));

            subscriptions.add(action);

            return () -> subscriptions.remove(action);
        } finally {
            this.callSubscriptionsHandler();
        }
    }

    private void callSubscriptionsHandler() {
        EventHandler<WorkerStateEvent> eventListenerHandler = e -> NullableUtils.executeNonNull(this.events,
                subs -> NullableUtils.executeNonNull(subs.get(e.getEventType()),
                        l -> l.iterator().forEachRemaining(Runnable::run)
                )
        );

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }
}
