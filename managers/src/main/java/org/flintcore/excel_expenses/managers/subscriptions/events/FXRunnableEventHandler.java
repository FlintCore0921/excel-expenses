package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.flintcore.excel_expenses.managers.subscriptions.consumers.SimpleSubscriptionConsumerHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class FXRunnableEventHandler
        extends SimpleSubscriptionConsumerHandler<EventType<WorkerStateEvent>, Runnable>
        implements IRunnableEventSubscriptionFxHolder<WorkerStateEvent> {
}
