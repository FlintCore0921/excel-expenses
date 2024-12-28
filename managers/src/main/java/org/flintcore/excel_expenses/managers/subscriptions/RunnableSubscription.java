package org.flintcore.excel_expenses.managers.subscriptions;

import javafx.util.Subscription;
import lombok.Builder;

import java.util.function.Consumer;

@Builder
public class RunnableSubscription implements Runnable, Subscription {
    private final Runnable onRun;
    private final Consumer<Subscription> onUnsubscribe;

    @Override
    public void run() {
        this.onRun.run();
    }

    @Override
    public void unsubscribe() {
        this.onUnsubscribe.accept(this);
    }
}
