package org.flintcore.excel_expenses.models.subscriptions;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.*;

@Component
@Scope("prototype")
public class SubscriptionHolder implements Closeable {
    private Map<Object, Set<Subscription>> subscriptions;

    public void appendSubscriptionOn(Object key, Subscription scheduled) {
        initSubscriptions();

        this.subscriptions.computeIfAbsent(key, e -> Collections.synchronizedSet(new HashSet<>()))
                .add(scheduled);
    }

    private void initSubscriptions() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new HashMap<>());
    }

    public void remove(Object key) {
        NullableUtils.executeNonNull(this.subscriptions, mp -> mp.remove(key));
    }

    public void close() {
        NullableUtils.executeNonNull(this.subscriptions, subs -> {
            subs.values().forEach(
                    l -> l.forEach(Subscription::unsubscribe)
            );
            subs.clear();
        });
    }
}
