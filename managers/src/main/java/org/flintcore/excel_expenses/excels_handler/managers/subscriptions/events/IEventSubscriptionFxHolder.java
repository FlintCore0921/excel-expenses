package org.flintcore.excel_expenses.excels_handler.managers.subscriptions.events;

import javafx.event.Event;
import javafx.event.EventType;
import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.ISubscriptionHolder;
/**Schedule tasks for actions inside the FX Thread by Event calls.*/
public interface IEventSubscriptionFxHolder<T extends Event, R>
        extends ISubscriptionHolder<EventType<T>, R> {}
