package org.flintcore.excel_expenses.managers.subscriptions.handlers;

import data.utils.NullableUtils;
import javafx.application.Platform;
import javafx.util.Subscription;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class DefaultKeylessFxHandler implements Runnable {
    protected final Subscription DEFAULT_VALUE = () -> {
        // Nothing
    };

    protected Set<Runnable> handlers;

    @Override
    public void run() {
        NullableUtils.executeNonNull(this.handlers,
                hr -> hr.forEach(Platform::runLater));
    }

    protected void initHolder() {
        NullableUtils.executeIsNull(this.handlers,
                () -> this.handlers = new CopyOnWriteArraySet<>()
        );
    }
}
