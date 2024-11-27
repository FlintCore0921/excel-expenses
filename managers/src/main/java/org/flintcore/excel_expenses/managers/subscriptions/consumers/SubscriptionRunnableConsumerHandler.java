package org.flintcore.excel_expenses.managers.subscriptions.consumers;

import java.io.IOException;

/**
 * T as event type, R as subscription task, L as Subscription task holder
 */
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
    public void close() throws IOException {
        this.generalSubscriptions.clear();
        this.lastSubscriptions.clear();
    }
}
