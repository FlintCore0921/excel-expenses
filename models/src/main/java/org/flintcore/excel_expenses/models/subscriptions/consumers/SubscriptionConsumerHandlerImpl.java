package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;

import java.util.*;
import java.util.function.Supplier;

public abstract class SubscriptionConsumerHandlerImpl<T, R, L>
        implements ISubscriptionConsumerHandler<T, R> {

    protected Map<T, L> subscriptions;
    protected Set<R> generalSubscriptions;
    protected Map<T, L> lastSubscriptions;

    @Override
    public Subscription addGeneralSubscription(R consumer) {
        initGeneralSubscriptionHolder();
        this.generalSubscriptions.add(consumer);

        return () -> this.generalSubscriptions.remove(consumer);
    }

    // Initializers

    protected void initSubscriptionHolder(Supplier<Map<T, L>> initializer) {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = initializer.get());
    }

    protected abstract void initSubscriptionHolder();

    protected abstract void initGeneralSubscriptionHolder();

    protected abstract void initLastSubscriptionHolder();

    // Interact with values

    protected void triggerHandlers(Iterable<R> generalSubscriptions) {
        NullableUtils.executeNonNull(generalSubscriptions,
                l -> l.iterator().forEachRemaining(this::handleValue)
        );
    }

    protected void triggerGeneralHandlers() {
        initGeneralSubscriptionHolder();
        triggerHandlers(generalSubscriptions);
    }

    protected abstract void handleValue(R value);
}
