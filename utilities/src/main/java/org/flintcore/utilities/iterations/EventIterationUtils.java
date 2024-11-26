package org.flintcore.utilities.iterations;

import data.utils.NullableUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.Map;

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
}
