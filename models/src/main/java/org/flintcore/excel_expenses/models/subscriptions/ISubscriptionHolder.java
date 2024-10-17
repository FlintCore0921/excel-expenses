package org.flintcore.excel_expenses.models.subscriptions;

import javafx.event.Event;
import javafx.util.Subscription;

import java.util.List;

public interface ISubscriptionHolder<T, R> {
    default Subscription addSubscription(List<T> types, R action) {
        return types.stream()
                .map(e -> addSubscription(e, action))
                .reduce(Subscription::combine)
                .orElseThrow();
    }

     Subscription addSubscription(T type, R action);
}

