package org.flintcore.excel_expenses.configurations.receipts;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.utilities.lists.FutureFXListUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FutureUtils;

import java.util.concurrent.Future;

@Service
@Log4j2
// TODO
public class ReceiptFileFXService implements ISaveFxServiceStatus, ILoaderFxServiceStatus {
    private final ReceiptSaveTaskService receiptSaveTaskService;
    private final ReceiptRequestTaskService receiptRequestTaskService;

    protected ObservableSet<Receipt> dataSetList;

    public ReceiptFileFXService(
            // TODO
//            FXRunnableEventHandler fxRunnableEventHandler,
            ReceiptSaveTaskService receiptSaveTaskService,
            ReceiptRequestTaskService receiptRequestTaskService
    ) {
//        super(subscriptionManager, shutDownSubscriptionHolder, fxRunnableEventHandler);
        this.receiptSaveTaskService = receiptSaveTaskService;
        this.receiptRequestTaskService = receiptRequestTaskService;
    }

    public Future<Boolean> saveData(Receipt receipt) {
        return FutureUtils.callAsync(() -> this.dataSetList.add(receipt));
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.receiptRequestTaskService.isRequestingProperty();
    }

    @Override
    public ReadOnlyBooleanProperty isSavingProperty() {
        return this.receiptSaveTaskService.isSavingProperty();
    }

    // TODO Receipt interface Implementation.
    public Future<Boolean> deleteData(Receipt receipt) {
        return FutureUtils.callAsync(() -> this.dataSetList.remove(receipt));
    }

    // TODO Receipt interface Implementation.
    public Future<ObservableList<Receipt>> getDataList() {
        initObservableList();
        // TODO
        return FutureFXListUtils.getListFrom(this.dataSetList);
    }

    protected void initObservableList() {
        this.dataSetList = FXCollections.observableSet();

        // Supply new data when save is requested!
        this.receiptSaveTaskService.setLocalBusinessSupplier(
                () -> SerialListHolder.from(this.dataSetList)
        );

        // Request data on service.
        this.receiptRequestTaskService.handle(TaskFxEvent.WORKER_STATE_SUCCEEDED, () -> {
            this.dataSetList.addAll(this.receiptRequestTaskService.getValue());
        });

        this.receiptRequestTaskService.restart();
    }
}