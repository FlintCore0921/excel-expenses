package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class SubscriptionConsumerHandlerImpl<T, R, L> implements ISubscriptionConsumerHandler<T, R> {
    protected Map<T, L> subscriptions;

    protected void initSubscriptionHolder(Supplier<Map<T, L>> initializer) {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = initializer.get());
    }

    protected void initSubscriptionHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new HashMap<>());
    }
}
