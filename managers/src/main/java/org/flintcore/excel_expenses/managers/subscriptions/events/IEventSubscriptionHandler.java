package org.flintcore.excel_expenses.managers.subscriptions.events;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.util.Subscription;

import java.util.List;
import java.util.function.Consumer;

public interface IEventSubscriptionHandler<K extends Event, R> extends Consumer<EventType<K>> {
    Subscription handle(EventType<K> key, R value);
    Subscription handle(List<EventType<K>> keys, R value);
}
