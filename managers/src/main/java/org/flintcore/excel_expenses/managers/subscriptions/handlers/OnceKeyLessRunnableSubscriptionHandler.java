package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import lombok.NonNull;
import org.flintcore.excel_expenses.managers.subscriptions.OnceRunnableSubscription;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@Primary
public final class OnceKeyLessRunnableSubscriptionHandler
        extends DefaultKeylessFxHandler
        implements IOnceKeyLessSubscriptionHandler<Runnable> {

    @Override
    public Subscription handleOnce(@NonNull Runnable value) {
        this.initHolder();

        OnceRunnableSubscription subscription = OnceRunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(this.handlers::remove)
                .build();

        this.handlers.add(subscription);

        return subscription;
    }

    @Override
    public Subscription handleOnce(@NonNull List<Runnable> values) {
        if (values.isEmpty()) return DEFAULT_VALUE;

        return values.stream().map(this::handleOnce)
                .reduce(Subscription::and)
                .orElse(DEFAULT_VALUE);
    }
}
