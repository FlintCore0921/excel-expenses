package org.flintcore.excel_expenses.managers.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionFxHolder;
import org.flintcore.utilities.iterations.EventIterationUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@RequiredArgsConstructor
public abstract class ObservableFXService<T> extends Service<T>
        implements IEventSubscriptionFxHolder<WorkerStateEvent, Runnable> {

    /**
     * Use {@link #getEventListenerHolder} to avoid and ensure field not empty.
     */
    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.listenSubscription(type, action);
    }

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> getEventListenerHolder() {
        NullableUtils.executeIsNull(this.events, () -> {
            this.events = new ConcurrentHashMap<>();
            this.setupSubscriptionsHandler();
        });

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

    protected void setupSubscriptionsHandler() {
        EventHandler<WorkerStateEvent> eventListenerHandler = EventIterationUtils
                .onHandleRunnableEvents(this.events);

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }
}
