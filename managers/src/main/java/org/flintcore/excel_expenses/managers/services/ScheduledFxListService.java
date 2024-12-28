package org.flintcore.excel_expenses.managers.services;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;

import java.util.List;

@Log4j2
public abstract class ScheduledFxListService<T> extends ScheduledFxService<List<T>> {
    public ScheduledFxListService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutDownHolder
    ) {
        super(eventHandler, shutDownHolder);
    }
}
