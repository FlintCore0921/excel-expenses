package org.flintcore.excel_expenses.managers.subscriptions.consumers;

import data.utils.NullableUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SubscriptionRunnableConsumerHandler<T, R extends Runnable, L>
        extends SubscriptionConsumerHandler<T, R, L> {

    @Override
    protected void handleValue(R value) {
        value.run();
    }

    /**
     * Similar to {@link SubscriptionConsumerHandler#addOneTimeSubscription addOneTimeSubscription}.
     * This implementation may trigger if the flag {@code mayTrigger} and the handler was trigger recently.
     */
    public void addOneTimeSubscription(T type, R action, boolean mayTrigger) {
        if (mayTrigger && this.isOneTimeCalled.get()) {
            action.run();
            return;
        }

        super.addOneTimeSubscription(type, action);
    }

    @Override
    protected void initSubscriptionHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new ConcurrentHashMap<>());
    }

    @Override
    protected void initGeneralSubscriptionHolder() {
        NullableUtils.executeIsNull(this.generalSubscriptions,
                () -> this.generalSubscriptions = new CopyOnWriteArraySet<>()
        );
    }

    @Override
    protected void initOneTimeSubscriptionHolder() {
        NullableUtils.executeIsNull(this.generalSubscriptions,
                () -> {
                    this.oneTimeSubscriptions = new CopyOnWriteArraySet<>();
                    this.isOneTimeCalled = new AtomicBoolean();
                }
        );
    }

    @Override
    protected void initLastSubscriptionHolder() {
        NullableUtils.executeIsNull(this.lastSubscriptions,
                () -> this.lastSubscriptions = new ConcurrentHashMap<>()
        );
    }

    @Override
    public void close() throws IOException {
        this.subscriptions.clear();
        this.generalSubscriptions.clear();
        this.lastSubscriptions.clear();
    }
}
