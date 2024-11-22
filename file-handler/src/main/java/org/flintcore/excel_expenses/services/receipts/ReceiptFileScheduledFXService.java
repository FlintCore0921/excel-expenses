package org.flintcore.excel_expenses.services.receipts;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.services.ScheduledFxService;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class ReceiptFileScheduledFXService extends ScheduledFxService<Receipt> {
    /**
     * Just the same as {@link #storeTaskService} but as original impl type.
     */
    private final ReceiptSaveTaskService receiptSaveTaskService;

    public ReceiptFileScheduledFXService(
            ReceiptSaveTaskService receiptSaveTaskService,
            ReceiptRequestTaskService requestTaskService,
            SubscriptionHolder subscriptionManager,
            ApplicationScheduler appScheduler,
            ShutdownFXApplication shutDownSubscriptionHolder
    ) {
        super(requestTaskService, receiptSaveTaskService, subscriptionManager,
                appScheduler, shutDownSubscriptionHolder);
        this.receiptSaveTaskService = receiptSaveTaskService;
    }

    protected void initObservableList() {
        super.initObservableList();

        this.receiptSaveTaskService.setLocalBusinessSupplier(
                () -> SerialListHolder.from(this.dataSetList)
        );
    }
}