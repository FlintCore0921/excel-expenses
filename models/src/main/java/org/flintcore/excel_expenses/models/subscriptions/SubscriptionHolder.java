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
        this.subscriptions.computeIfAbsent(key, e -> new HashSet<>())
                .add(scheduled);
    }

    public void remove(Object key){
        NullableUtils.executeNonNull(this.subscriptions, mp -> mp.remove(key));
    }

    public void close() {
        this.subscriptions.values().forEach(
                l -> l.forEach(Subscription::unsubscribe)
        );
        this.subscriptions.clear();
    }
}
