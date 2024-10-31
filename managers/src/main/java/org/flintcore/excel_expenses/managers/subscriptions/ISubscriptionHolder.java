package org.flintcore.excel_expenses.managers.subscriptions;

import javafx.util.Subscription;
import lombok.NonNull;

import java.io.Closeable;
import java.util.List;

public interface ISubscriptionHolder<T, R> extends Closeable {

    Subscription DEFAULT_RESPONSE = () -> {};

    default Subscription addSubscription(@NonNull List<T> types, R action) {
        if (types.isEmpty()) return DEFAULT_RESPONSE;

        return types.stream()
                .map(e -> addSubscription(e, action))
                .reduce(Subscription::combine)
                .orElse(DEFAULT_RESPONSE);
    }

    default void addOneTimeSubscription(@NonNull List<T> types, R action) {
        types.forEach(e -> addOneTimeSubscription(e, action));
    }

    Subscription addSubscription(T type, R action);

    void addOneTimeSubscription(T type, R action);
}
