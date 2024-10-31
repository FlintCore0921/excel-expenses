package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import javafx.collections.ObservableList;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.subscriptions.ShutdownSubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.services.FileScheduledFxService;
import org.flintcore.excel_expenses.tasks.receipts.ReceiptRequestTaskService;
import org.flintcore.excel_expenses.tasks.receipts.ReceiptSaveTaskService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
public class ReceiptFileScheduledFXService extends FileScheduledFxService<Receipt> {
    /**
     * Just the same as {@link #storeTaskService} but as original impl type.
     */
    private final ReceiptSaveTaskService receiptSaveTaskService;

    public ReceiptFileScheduledFXService(
            ReceiptSaveTaskService receiptSaveTaskService,
            ReceiptRequestTaskService requestTaskService,
            SubscriptionHolder subscriptionManager,
            ApplicationScheduler appScheduler,
            ShutdownSubscriptionHolder shutDownSubscriptionHolder
    ) {
        super(requestTaskService, receiptSaveTaskService, subscriptionManager,
                appScheduler, shutDownSubscriptionHolder);
        this.receiptSaveTaskService = receiptSaveTaskService;
    }

    @Override
    public CompletableFuture<ObservableList<Receipt>> getDataList() {
        NullableUtils.executeIsNull(this.dataSetList, this::requestData);
        return CompletableFuture.completedFuture(
                readOnlyListWrapper.getReadOnlyProperty()
        );
    }

    @Override
    protected void initObservableList() {
        super.initObservableList();

        if (Objects.isNull(this.readOnlyListWrapper)) return;

        this.receiptSaveTaskService.setLocalBusinessSupplier(
                () -> SerialListHolder.from(this.readOnlyListWrapper.getReadOnlyProperty())
        );
    }
}