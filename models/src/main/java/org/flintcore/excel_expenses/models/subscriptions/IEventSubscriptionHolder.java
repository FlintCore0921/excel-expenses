package org.flintcore.excel_expenses.models.subscriptions;

import javafx.event.Event;
import javafx.event.EventType;

public interface IEventSubscriptionHolder<T extends Event, R> extends ISubscriptionHolder<EventType<T>, R> {

}
