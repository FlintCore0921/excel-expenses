package org.flintcore.excel_expenses.models.subscriptions.consumers;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class MultiSubscriptionConsumerHandler<T, R extends Runnable>
        extends SubscriptionConsumerHandlerImpl<T, R, Set<R>> {

    @Override
    public void accept(T t) {
        NullableUtils.executeNonNull(this.subscriptions.get(t),
                l -> l.iterator().forEachRemaining(Runnable::run)
        );
    }

    @Override
    public Subscription addSubscription(T type, R action) {
        initSubscriptionHolder(HashMap::new);

        Set<R> holder = this.subscriptions.computeIfAbsent(type, t -> new HashSet<>());
        holder.add(action);

        return () -> holder.remove(action);
    }
}
