package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Scope("prototype")
public class MultiSubscriptionConsumerHandler<T, R extends Runnable, L extends Collection<R>>
        extends SubscriptionRunnableConsumerHandler<T, R, L> {

    @Override
    public void accept(T t) {
        NullableUtils.executeNonNull(this.subscriptions,
                l -> NullableUtils.executeNonNull(l.get(t), this::triggerHandlers)
        );
        triggerGeneralHandlers();
        triggerOneTimeHandlers();
        NullableUtils.executeNonNull(this.lastSubscriptions,
                l -> NullableUtils.executeNonNull(l.get(t), this::triggerHandlers)
        );
    }

    @Override
    public Subscription addSubscription(T type, R action) {
        initSubscriptionHolder();
        L holder = this.subscriptions.computeIfAbsent(type, this::computeResult);
        holder.add(action);

        return () -> holder.remove(action);
    }

    @Override
    public void addOneTimeSubscription(T type, R action) {
        initOneTimeSubscriptionHolder();
        this.oneTimeSubscriptions.add(action);
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
        return (L) new CopyOnWriteArraySet<R>();
    }

    @Override
    protected void triggerHandlers(Iterable<R> generalSubscriptions) {
        NullableUtils.executeNonNull(generalSubscriptions, super::triggerHandlers);
    }
}
