package org.flintcore.excel_expenses.models.subscriptions.events;

import javafx.event.Event;
import javafx.event.EventType;
import org.flintcore.excel_expenses.models.subscriptions.ISubscriptionHolder;

public interface IEventSubscriptionHolder<T extends Event, R> extends ISubscriptionHolder<EventType<T>, R> {

}
