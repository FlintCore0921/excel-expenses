package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SubscriptionConsumerHandler<T, R extends Runnable> extends SubscriptionConsumerHandlerImpl<T, R, R> {
    @Override
    public void accept(T t) {
        NullableUtils.executeNonNull(this.subscriptions.get(t), Runnable::run);
    }

    @Override
    public Subscription addSubscription(T type, R action) {
        initSubscriptionHolder();
        this.subscriptions.put(type, action);
        return () -> this.subscriptions.remove(type);
    }
}
