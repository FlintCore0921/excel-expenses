package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import lombok.NonNull;

import java.util.List;

public interface IKeyLessSubscriptionHandler<R> extends Runnable {
    Subscription handle(@NonNull R value);
    Subscription handle(@NonNull List<R> values);
}
