package org.flintcore.excel_expenses.services.business;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.business.IBusinessLoaderService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.ScheduledFxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

@Service
@Log4j2
public class LocalBusinessFileFXService extends ScheduledFxService<LocalBusiness>
        implements IBusinessLoaderService<LocalBusiness> {
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

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        return getDataList().thenApply(Function.identity());
    }
}
