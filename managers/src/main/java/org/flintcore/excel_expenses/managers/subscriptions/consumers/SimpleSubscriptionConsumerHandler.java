package org.flintcore.excel_expenses.managers.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SimpleSubscriptionConsumerHandler<T, R extends Runnable>
        extends SubscriptionRunnableConsumerHandler<T, R, R> {
    @Override
    public void accept(T t) {
        NullableUtils.executeNonNull(this.subscriptions,
                l -> NullableUtils.executeNonNull(l.get(t), Runnable::run)
        );
        triggerGeneralHandlers();
        triggerOneTimeHandlers();
        NullableUtils.executeNonNull(this.lastSubscriptions,
                l -> NullableUtils.executeNonNull(l.get(t), Runnable::run)
        );
    }

    @Override
    public Subscription addSubscription(T type, R action) {
        initSubscriptionHolder();
        this.subscriptions.put(type, action);
        return () -> this.subscriptions.remove(type);
    }

    @Override
    public Subscription addLastSubscription(T t, R listener) {
        initLastSubscriptionHolder();
        this.lastSubscriptions.put(t, listener);
        return () -> this.lastSubscriptions.remove(t);
    }
}
