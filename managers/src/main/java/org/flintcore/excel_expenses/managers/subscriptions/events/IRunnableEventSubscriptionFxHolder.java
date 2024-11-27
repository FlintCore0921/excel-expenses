package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.event.Event;
import javafx.event.EventType;
import org.flintcore.excel_expenses.managers.subscriptions.ISubscriptionHolder;

/**Schedule tasks for actions inside the FX Thread by Event calls.*/
public interface IRunnableEventSubscriptionFxHolder<E extends Event>
        extends ISubscriptionHolder<EventType<E>, Runnable> {}
