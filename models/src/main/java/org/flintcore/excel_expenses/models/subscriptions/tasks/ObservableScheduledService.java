package org.flintcore.excel_expenses.models.subscriptions.tasks;

import data.utils.NullableUtils;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;
import org.flintcore.utilities.iterations.EventIterationUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Task that can be scheduled in a period of time.
 */
public abstract class ObservableScheduledService<T> extends ScheduledService<T>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    public static final Duration DEFAULT_RANGE_PERIOD = Duration.minutes(5);
    /**
     * Use {@link #getEventListenerHolder()} to avoid and ensure field not empty.
     */
    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> events;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.listenSubscription(type, action);
    }

    protected Map<EventType<WorkerStateEvent>, Set<Runnable>> getEventListenerHolder() {
        initSubscriptionsHolder();
        return events;
    }

    protected Subscription listenSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        Set<Runnable> subscriptions = getEventListenerHolder()
                .computeIfAbsent(type, this::buildSubscriptionHolder);

        subscriptions.add(action);

        return () -> subscriptions.remove(action);
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

    protected void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.events, () -> {
            this.events = new ConcurrentHashMap<>();
            setupSubscriptionsHandler();
        });
    }
}
