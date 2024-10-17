package org.flintcore.excel_expenses.models.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.models.subscriptions.IEventSubscriptionHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public abstract class ObservableTask<T> extends Task<T>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    private Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.listenSubscription(type, action);
    }

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> getEventListenerHolder() {
        NullableUtils.executeIsNull(this.events, () -> this.events = new HashMap<>());
        return events;
    }

    protected Subscription listenSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        try {
            Set<Runnable> subscriptions = getEventListenerHolder()
                    .computeIfAbsent(type, __ -> new HashSet<>());

            subscriptions.add(action);

            return () -> subscriptions.remove(action);
        } finally {
            this.callSubscriptionsHandler();
        }
    }

    private void callSubscriptionsHandler() {
        EventHandler<WorkerStateEvent> eventListenerHandler = e -> NullableUtils.executeNonNull(this.events,
                subs -> NullableUtils.executeNonNull(subs.get(e.getEventType()),
                        l -> l.forEach(Runnable::run)
                )
        );

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }
}
