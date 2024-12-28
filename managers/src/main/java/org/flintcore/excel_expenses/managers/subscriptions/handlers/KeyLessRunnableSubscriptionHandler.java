package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import lombok.NonNull;
import org.flintcore.excel_expenses.managers.subscriptions.RunnableSubscription;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("prototype")
@Primary
public final class KeyLessRunnableSubscriptionHandler
        extends DefaultKeylessFxHandler
        implements IKeyLessSubscriptionHandler<Runnable> {
    @Override
    public Subscription handle(@NonNull Runnable value) {
        this.initHolder();

        RunnableSubscription subscription = RunnableSubscription.builder()
                .onRun(value)
                .onUnsubscribe(this.handlers::remove)
                .build();

        this.handlers.add(subscription);

        return subscription;
    }

    @Override
    public Subscription handle(@NonNull List<Runnable> values) {
        if (values.isEmpty()) return DEFAULT_VALUE;

        return values.stream().map(this::handle)
                .reduce(Subscription::and)
                .orElse(DEFAULT_VALUE);
    }
}
