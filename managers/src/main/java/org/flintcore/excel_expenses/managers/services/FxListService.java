package org.flintcore.excel_expenses.managers.services;

import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;

@Log4j2
public abstract class FxListService<T> extends FxService<ObservableList<T>> {
    public FxListService(GeneralEventSubscriptionHandler subscriptionManager,
                         ShutdownFXApplication shutDownHolder) {
        super(subscriptionManager, shutDownHolder);
    }
}
