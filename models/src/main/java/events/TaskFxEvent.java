package events;

import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;

import java.util.List;

public final class TaskFxEvent extends WorkerStateEvent {
    public static final List<EventType<WorkerStateEvent>> WORKER_STATE_DONE = List.of(
            WORKER_STATE_RUNNING, WORKER_STATE_FAILED, WORKER_STATE_CANCELLED
    );

    /** Cannot instantiate. */
    private TaskFxEvent(Worker worker, EventType<? extends WorkerStateEvent> eventType) {
        super(worker, eventType);
    }
}
