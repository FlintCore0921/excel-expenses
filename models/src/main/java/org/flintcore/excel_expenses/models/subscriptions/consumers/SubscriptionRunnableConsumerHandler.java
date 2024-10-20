package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;

import java.util.HashMap;
import java.util.HashSet;

public abstract class SubscriptionRunnableConsumerHandler<T, R extends Runnable, L>
        extends SubscriptionConsumerHandlerImpl<T, R, L> {
    @Override
    protected void handleValue(R value) {
        value.run();
    }

    @Override
    public void accept(T t) {
        triggerGeneralHandlers();
    }

    @Override
    protected void initSubscriptionHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new HashMap<>());
    }

    @Override
    protected void initGeneralSubscriptionHolder() {
        NullableUtils.executeIsNull(this.generalSubscriptions,
                () -> this.generalSubscriptions = new HashSet<>()
        );
    }

    @Override
    protected void initLastSubscriptionHolder() {
        NullableUtils.executeIsNull(this.lastSubscriptions,
                () -> this.lastSubscriptions = new HashMap<>()
        );
    }
}
