package org.flintcore.excel_expenses.services;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;

import java.util.List;

@Log4j2
public abstract class FxListService<T> extends FxService<List<T>> {
    public FxListService(SubscriptionHolder subscriptionManager, ShutdownFXApplication shutDownHolder, FXRunnableEventHandler eventHandler) {
        super(subscriptionManager, shutDownHolder, eventHandler);
    }
}
