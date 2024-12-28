package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.event.EventType;
import javafx.util.Subscription;

import java.util.List;
import java.util.function.Consumer;

public interface IOnceSubscriptionHandler<K, V> extends Consumer<K> {
    Subscription handleOnce(K key, V r);
    Subscription handleOnce(List<K> keys, V r);

}
