package org.flintcore.excel_expenses.managers.tasks;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IRunnableEventSubscriptionFxHolder;

import java.util.Objects;

@Log4j2
public abstract class ObservableFXTask<T> extends Task<T>
        implements IRunnableEventSubscriptionFxHolder<WorkerStateEvent> {

    protected FXRunnableEventHandler eventHandler;

    @Override
    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.eventHandler.addSubscription(type, action);
    }

    protected Subscription listenSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.eventHandler.addSubscription(type, action);
    }

    protected void setupSubscriptionsHandler() {
        if (Objects.nonNull(getOnScheduled())) return;

        EventHandler<WorkerStateEvent> eventListenerHandler = event -> {
            EventType<? extends Event> eventType = event.getEventType();

            if (eventType.getSuperType() != WorkerStateEvent.ANY) return;
            log.info(eventType.toString());
            this.eventHandler.accept((EventType<WorkerStateEvent>) eventType);
        };

        setOnSucceeded(eventListenerHandler);
        setOnFailed(eventListenerHandler);
        setOnScheduled(eventListenerHandler);
        setOnCancelled(eventListenerHandler);
        setOnRunning(eventListenerHandler);
    }
}
