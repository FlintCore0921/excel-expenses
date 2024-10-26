package org.flintcore.excel_expenses.models.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Task that can be scheduled in a period of time.
 */
public abstract class ObservableScheduledService<T> extends ScheduledService<T>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

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
                    .computeIfAbsent(type, this::buildSubscriptionHolder);

            subscriptions.add(action);

            return () -> subscriptions.remove(action);
        } finally {
            this.setupSubscriptionsHandler();
        }
    }

    protected Set<Runnable> buildSubscriptionHolder(EventType<WorkerStateEvent> __) {
        return new CopyOnWriteArraySet<>();
    }

    protected void setupListeners() {
        setupSubscriptionsHandler();
    }

    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;

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

    protected void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.events,
                () -> this.events = new ConcurrentHashMap<>());
    }
}
