package org.flintcore.excel_expenses.excels_handler.managers.shutdowns;

import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.ISubscriptionHolder;

import java.io.Closeable;

public interface IShutdownHandler<R> extends ISubscriptionHolder<Object, R>, Closeable {
    default void shutdown(){
        close();
    }

    @Override
    void close();
}
