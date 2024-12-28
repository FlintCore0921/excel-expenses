package org.flintcore.excel_expenses.managers.tasks;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.utilities.iterations.EventIterationUtils;

import java.util.List;
import java.util.Objects;

@Log4j2
public abstract class ObservableTask<T> extends Task<T>
        implements IEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    protected GeneralEventSubscriptionHandler eventHandler;

    public ObservableTask(GeneralEventSubscriptionHandler eventHandler) {
        this.eventHandler = eventHandler;
        this.setupSubscriptionsHandler();
    }

    @Override
    public Subscription handle(EventType<WorkerStateEvent> key, Runnable value) {
        return this.eventHandler.handle(key, value);
    }

    @Override
    public Subscription handle(List<EventType<WorkerStateEvent>> keys, Runnable value) {
        return this.eventHandler.handle(keys, value);
    }

    @Override
    public void accept(EventType<WorkerStateEvent> key) {
        this.eventHandler.accept(key);
    }

    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;
        EventIterationUtils.appendListenerTo(this, this.eventHandler);
    }

    private void extracted(ObservableTask<T> task) {
    }
}
