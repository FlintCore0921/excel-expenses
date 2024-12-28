package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;

import java.util.List;
import java.util.function.Consumer;

public interface ISubscriptionHandler<K, V> extends Consumer<K> {
    Subscription handle(K key, V value);

    Subscription handle(List<K> keys, V value);
}
