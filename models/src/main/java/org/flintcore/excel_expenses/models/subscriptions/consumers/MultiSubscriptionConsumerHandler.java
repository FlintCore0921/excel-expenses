package org.flintcore.excel_expenses.models.subscriptions.consumers;

import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
public class MultiSubscriptionConsumerHandler<T, R extends Runnable, L extends Set<R>>
        extends SubscriptionRunnableConsumerHandler<T, R, L> {

    @Override
    public void accept(T t) {
        triggerHandlers(this.subscriptions.get(t));
        super.accept(t);
        triggerHandlers(this.lastSubscriptions.get(t));
    }

    @Override
    public Subscription addSubscription(T type, R action) {
        initSubscriptionHolder(HashMap::new);
        L holder = this.subscriptions.computeIfAbsent(type, this::computeResult);
        holder.add(action);

        return () -> holder.remove(action);
    }

    @Override
    public Subscription addLastSubscription(T t, R listener) {
        initLastSubscriptionHolder();
        L holder = this.lastSubscriptions.computeIfAbsent(t, this::computeResult);
        holder.add(listener);
        return () -> holder.remove(listener);
    }

    @SuppressWarnings("unchecked")
    protected L computeResult(T _key) {
        return (L) new HashSet<R>();
    }
}
