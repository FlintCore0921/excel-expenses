package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscription;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscriptionHolder;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Scope("prototype")
@Primary
public final class DynamicSubscriptionHandler<K>
        extends RunnableSubscriptionHolder<K>
        implements ISubscriptionHandler<K, Runnable> {

    private Map<K, Set<Runnable>> holders;

    @Override
    public Subscription handle(K key, Runnable value) {
        initHolder();

        Set<Runnable> runnables = this.holders.computeIfAbsent(key, this::buildHolder);

        RunnableSubscription runnableSubscription = RunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(runnables::remove)
                .build();

        runnables.add(value);

        return runnableSubscription;
    }

    @Override
    public Subscription handle(List<K> keys, Runnable value) {
        return this.handleMulti(keys, value, this::handle);
    }
}
