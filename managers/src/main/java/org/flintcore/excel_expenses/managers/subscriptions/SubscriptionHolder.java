package org.flintcore.excel_expenses.managers.subscriptions;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import lombok.NonNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class SubscriptionHolder implements Closeable {
    private Map<Object, Set<Subscription>> subscriptions;

    public void appendSubscriptionOn(Object key, @NonNull Subscription scheduled) {
        initSubscriptions();

        this.subscriptions.computeIfAbsent(key, this::buildListenerHolder)
                .add(scheduled);
    }

    private Set<Subscription> buildListenerHolder(Object k) {
        return Collections.synchronizedSet(new HashSet<>());
    }

    public void appendSubscriptionOn(Object key, List<Subscription> schedules) {
        NullableUtils.executeNonNull(schedules,
                l -> l.forEach(s -> appendSubscriptionOn(key, s))
        );
    }

    public void remove(Object key) {
        NullableUtils.executeNonNull(this.subscriptions,
                mp -> mp.remove(key)
        );
    }

    /**
     * Clear all the subscriptions held by this class.
     */
    public void close() {
        NullableUtils.executeNonNull(this.subscriptions,
                subs -> subs.keySet().iterator().forEachRemaining(this::close)
        );
    }

    public void close(Object key) {
        NullableUtils.executeNonNull(this.subscriptions, () -> stopSubscriptionsOn(key));
    }

    private void stopSubscriptionsOn(Object key) {
        NullableUtils.executeNonNull(this.subscriptions.remove(key), subsHolder -> {
            Iterator<Subscription> iterator = subsHolder.iterator();
            while (iterator.hasNext()) {
                Subscription subscription = iterator.next();
                subscription.unsubscribe();
                iterator.remove();
            }
        });
    }

    private void initSubscriptions() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new ConcurrentHashMap<>());
    }
}
