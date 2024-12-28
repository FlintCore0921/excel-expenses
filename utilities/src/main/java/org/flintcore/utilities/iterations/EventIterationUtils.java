package org.flintcore.utilities.iterations;

import data.utils.NullableUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.Map;
import java.util.function.Consumer;

public final class EventIterationUtils {
    private EventIterationUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static <
            E extends Event,
            ET extends EventType<E>,
            V extends Iterable<Runnable>
            > EventHandler<E> onHandleRunnableEvents(
            Map<ET, V> eventsHolder
    ) {

        return e -> NullableUtils.executeNonNull(eventsHolder,
                subs -> NullableUtils.executeNonNull(subs.get(e.getEventType()),
                        l -> l.iterator().forEachRemaining(Runnable::run)
                )
        );
    }

    public static <T> void appendListenerTo(Task<T> task, Consumer<EventType<WorkerStateEvent>> consumer) {
        EventHandler<WorkerStateEvent> eventListenerHandler = getEventListenerHandler(consumer);

        task.setOnSucceeded(eventListenerHandler);
        task.setOnFailed(eventListenerHandler);
        task.setOnScheduled(eventListenerHandler);
        task.setOnCancelled(eventListenerHandler);
        task.setOnRunning(eventListenerHandler);
    }

    public static <T> void appendListenerTo(Service<T> service, Consumer<EventType<WorkerStateEvent>> consumer) {
        EventHandler<WorkerStateEvent> eventListenerHandler = getEventListenerHandler(consumer);

        service.setOnSucceeded(eventListenerHandler);
        service.setOnFailed(eventListenerHandler);
        service.setOnScheduled(eventListenerHandler);
        service.setOnCancelled(eventListenerHandler);
        service.setOnRunning(eventListenerHandler);
    }

    @SuppressWarnings("unchecked")
    private static EventHandler<WorkerStateEvent> getEventListenerHandler(Consumer<EventType<WorkerStateEvent>> consumer) {
        return evt -> consumer
                .accept(((EventType<WorkerStateEvent>) evt.getEventType()));
    }

}
