package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public final class GeneralDynamicRunnableSubscriptionHandler<K>
        implements ISubscriptionHandler<K, Runnable>,
        IOnceSubscriptionHandler<K, Runnable> {

    private final DynamicSubscriptionHandler<K> dynamicHandler;
    private final DynamicOnceSubscriptionHandler<K> dynamicOnceHandler;

    @Override
    public Subscription handle(K key, Runnable value) {
        return this.dynamicHandler.handle(key, value);
    }

    @Override
    public Subscription handle(List<K> keys, Runnable value) {
        return this.dynamicHandler.handle(keys, value);
    }

    @Override
    public Subscription handleOnce(K key, Runnable r) {
        return this.dynamicOnceHandler.handleOnce(key, r);
    }

    @Override
    public Subscription handleOnce(List<K> keys, Runnable r) {
        return this.dynamicOnceHandler.handleOnce(keys, r);
    }

    @Override
    public void accept(K k) {
        this.dynamicHandler.accept(k);
        this.dynamicOnceHandler.accept(k);
    }
}
