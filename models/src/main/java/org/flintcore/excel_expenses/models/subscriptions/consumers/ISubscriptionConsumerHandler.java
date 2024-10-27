package org.flintcore.excel_expenses.models.subscriptions.consumers;

import javafx.util.Subscription;
import org.flintcore.excel_expenses.models.subscriptions.ISubscriptionHolder;

import java.util.function.Consumer;

public interface ISubscriptionConsumerHandler<T, R>
        extends ISubscriptionHolder<T, R>, Consumer<T> {
    Subscription addGeneralSubscription(R consumer);
    Subscription addLastSubscription(T t, R consumer);
}
