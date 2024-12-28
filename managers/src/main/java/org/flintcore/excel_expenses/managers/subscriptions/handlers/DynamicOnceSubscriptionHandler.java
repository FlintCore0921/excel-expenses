package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import org.flintcore.excel_expenses.managers.subscriptions.OnceRunnableSubscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscriptionHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Scope("prototype")
@Primary
public final class DynamicOnceSubscriptionHandler<K>
        extends RunnableSubscriptionHolder<K>
        implements IOnceSubscriptionHandler<K, Runnable> {

    @Override
    public Subscription handleOnce(K key, Runnable value) {
        this.initHolder();

        Set<Runnable> runnables = this.eventHandler.computeIfAbsent(key, this::buildHolder);

        OnceRunnableSubscription runnableSubscription = OnceRunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(runnables::remove)
                .build();

        runnables.add(value);

        return runnableSubscription;
    }

    @Override
    public Subscription handleOnce(List<K> keys, Runnable value) {
        return this.handleMulti(keys, value, this::handleOnce);
    }
}
