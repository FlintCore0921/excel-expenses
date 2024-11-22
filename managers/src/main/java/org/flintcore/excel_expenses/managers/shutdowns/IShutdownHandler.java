package org.flintcore.excel_expenses.managers.shutdowns;

import org.flintcore.excel_expenses.managers.subscriptions.ISubscriptionHolder;

import java.io.Closeable;

public interface IShutdownHandler<R> extends ISubscriptionHolder<Object, R>, Closeable {
    default void shutdown(){
        close();
    }

    @Override
    void close();
}
