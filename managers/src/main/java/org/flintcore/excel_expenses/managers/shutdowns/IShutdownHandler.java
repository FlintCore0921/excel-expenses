package org.flintcore.excel_expenses.managers.shutdowns;


import org.flintcore.excel_expenses.managers.subscriptions.handlers.IKeyLessSubscriptionHandler;

import java.io.Closeable;

public interface IShutdownHandler
        extends IKeyLessSubscriptionHandler<Runnable>, Closeable {

}
