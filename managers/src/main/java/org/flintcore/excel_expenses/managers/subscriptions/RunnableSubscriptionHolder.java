package org.flintcore.excel_expenses.managers.subscriptions;

import data.utils.NullableUtils;
import javafx.application.Platform;
import javafx.util.Subscription;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class RunnableSubscriptionHolder<K> implements Consumer<K> {
    protected static final Subscription DEFAULT_VALUE = () -> {
    };

    protected Map<K, Set<Runnable>> eventHandler;

    @Override
    public void accept(K key) {
        if (Objects.isNull(this.eventHandler)) return;

        Set<Runnable> tasks = this.eventHandler.get(key);

        if (Objects.isNull(tasks)) return;

        Platform.runLater(() -> {
            for (Runnable task : tasks) {
                task.run();
            }
        });
    }

    public Subscription handleMulti(List<K> keys, Runnable value, BiFunction<K, Runnable, Subscription> handler) {
        if (keys.isEmpty()) return DEFAULT_VALUE;

        return keys.stream().map(k -> handler.apply(k, value))
                .reduce(Subscription::and)
                .orElse(DEFAULT_VALUE);
    }

    protected void initHolder() {
        NullableUtils.executeIsNull(this.eventHandler,
                () -> this.eventHandler = new ConcurrentHashMap<>());
    }

    protected Set<Runnable> buildHolder(K _key) {
        return new CopyOnWriteArraySet<>();
    }
}
