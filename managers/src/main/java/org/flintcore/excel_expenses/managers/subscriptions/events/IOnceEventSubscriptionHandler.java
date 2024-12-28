package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.util.Subscription;

import java.util.List;
import java.util.function.Consumer;

public interface IOnceEventSubscriptionHandler<E extends Event, R> extends Consumer<EventType<E>> {
    Subscription handleOnce(EventType<E> key, R r);
    Subscription handleOnce(List<EventType<E>> keys, R r);
}
