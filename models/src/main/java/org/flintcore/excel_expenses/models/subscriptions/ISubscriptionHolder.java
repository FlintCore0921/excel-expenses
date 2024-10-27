package org.flintcore.excel_expenses.models.subscriptions;

import javafx.event.Event;
import javafx.util.Subscription;
import lombok.NonNull;

import java.io.Closeable;
import java.util.List;

public interface ISubscriptionHolder<T, R> extends Closeable {
    default Subscription addSubscription(@NonNull  List<T> types, R action) {
        return types.stream()
                .map(e -> addSubscription(e, action))
                .reduce(Subscription::combine)
                .orElseThrow();
    }

     Subscription addSubscription(T type, R action);
    void addOneTimeSubscription(T type, R action);
    default void addOneTimeSubscription(@NonNull List<T> types, R action) {
        types.forEach(e -> addOneTimeSubscription(e, action));
    }
}

