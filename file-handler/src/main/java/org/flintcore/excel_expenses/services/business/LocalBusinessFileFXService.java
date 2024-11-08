package org.flintcore.excel_expenses.services.business;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.FileScheduledFxService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class LocalBusinessFileFXService extends FileScheduledFxService<LocalBusiness> {
    /**
     * Just the same as {@link #storeTaskService} but as original impl type.
     */
    private final LocalBusinessSaveFileService localBusinessSaveTask;

    public LocalBusinessFileFXService(
            SubscriptionHolder subscriptionManager,
            LocalBusinessRequestFileService localBusinessRequestTask,
            LocalBusinessSaveFileService localBusinessSaveTask,
            ApplicationScheduler appScheduler,
            ShutdownFXApplication shutDownSubscriptionHolder
    ) {
        super(localBusinessRequestTask, localBusinessSaveTask, subscriptionManager,
                appScheduler, shutDownSubscriptionHolder);
        this.localBusinessSaveTask = localBusinessSaveTask;
    }
}
