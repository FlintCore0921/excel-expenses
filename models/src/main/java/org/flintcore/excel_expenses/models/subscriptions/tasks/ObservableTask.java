package org.flintcore.excel_expenses.models.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;
import org.flintcore.utilities.iterations.EventIterationUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@RequiredArgsConstructor
public abstract class ObservableTask<T> extends Task<T>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    /**
     * Use {@link #getEventListenerHolder} to avoid and ensure field not empty.
     */
    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.listenSubscription(type, action);
    }

    protected Subscription listenSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        Set<Runnable> subscriptions = getEventListenerHolder()
                .computeIfAbsent(type, this::buildSubscriptionHolder);

        subscriptions.add(action);

        return () -> subscriptions.remove(action);
    }

    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;

        EventHandler<WorkerStateEvent> eventListenerHandler = EventIterationUtils
                .onHandleRunnableEvents(this.events);

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> getEventListenerHolder() {
        initSubscriptionsHolder();
        return events;
    }

    protected void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.events, () -> {
            this.events = new ConcurrentHashMap<>();
            setupSubscriptionsHandler();
        });
    }

    protected Set<Runnable> buildSubscriptionHolder(EventType<WorkerStateEvent> __) {
        return new CopyOnWriteArraySet<>();
    }
}
