package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import javafx.util.Subscription;
import lombok.NonNull;

import java.util.List;

public interface IOnceKeyLessSubscriptionHandler<R> extends Runnable {
    Subscription handleOnce(@NonNull R value);
    Subscription handleOnce(@NonNull List<R> value);
}
