package org.flintcore.excel_expenses.managers.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class SubscriptionConsumerHandler<T, R, L>
        implements ISubscriptionConsumerHandler<T, R> {

    protected Map<T, L> subscriptions;
    protected Collection<R> generalSubscriptions;
    protected Collection<R> oneTimeSubscriptions;
    protected AtomicBoolean isOneTimeCalled;
    protected Map<T, L> lastSubscriptions;

    @Override
    public Subscription addGeneralSubscription(R consumer) {
        initGeneralSubscriptionHolder();
        this.generalSubscriptions.add(consumer);

        return () -> this.generalSubscriptions.remove(consumer);
    }

    @Override
    public void addOneTimeSubscription(T type, R action) {
        initOneTimeSubscriptionHolder();
        this.oneTimeSubscriptions.add(action);
    }

    // Initializers

    protected void initSubscriptionHolder(Supplier<Map<T, L>> initializer) {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = initializer.get()
        );
    }

    /**
     * This function will be used to create the holder of subscriptions.
     *
     * @apiNote Recommend be a concurrent Map for iterations and self detach from holder.
     */
    protected abstract void initSubscriptionHolder();

    /**
     * This function will be used to create the holder of subscriptions.
     *
     * @apiNote Recommend be a concurrent Map for iterations and self detach from holder.
     */
    protected abstract void initOneTimeSubscriptionHolder();

    /**
     * This function will be used to create the holder of general subscriptions.
     *
     * @apiNote Recommend be a concurrent Map for iterations and self detach from holder.
     */
    protected abstract void initGeneralSubscriptionHolder();

    /**
     * This function will be used to create the holder of last call subscriptions.
     *
     * @apiNote Recommend be a concurrent Map for iterations and self detach from holder.
     */
    protected abstract void initLastSubscriptionHolder();

    // Interact with values

    /**
     * Rely on {@link java.util.Iterator Iterators} to call all subscriptions.
     */
    protected void triggerHandlers(Iterable<R> generalSubscriptions) {
        NullableUtils.executeNonNull(generalSubscriptions,
                l -> l.iterator().forEachRemaining(this::handleValue)
        );
    }

    protected void triggerOneTimeHandlers() throws UnsupportedOperationException {
        NullableUtils.executeNonNull(this.oneTimeSubscriptions, ot -> {
            this.isOneTimeCalled.set(true);
            this.triggerHandlers(ot);
            this.isOneTimeCalled.set(false);
            ot.clear();
        });
    }

    protected void triggerGeneralHandlers() {
        NullableUtils.executeNonNull(this.generalSubscriptions, this::triggerHandlers);
    }

    protected abstract void handleValue(R value);

}
