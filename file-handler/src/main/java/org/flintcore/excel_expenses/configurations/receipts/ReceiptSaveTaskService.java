package org.flintcore.excel_expenses.configurations.receipts;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptSerializeFileManager;
import org.flintcore.excel_expenses.files.receipts.ReceiptSerializeFileManager;
import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.ScheduledFxService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Log4j2
@Service
public class ReceiptSaveTaskService extends ScheduledFxService<Void>
        implements ISaveFxServiceStatus, IEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    private final ReceiptSerializeFileManager<Receipt> receiptFileManager;

    @Setter
    private Supplier<SerialListHolder<Receipt>> localBusinessSupplier;

    public ReceiptSaveTaskService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutdownFXApplication,
            PeriodReceiptSerializeFileManager receiptFileManager
    ) {
        super(eventHandler, shutdownFXApplication);
        this.receiptFileManager = receiptFileManager;
    }

    @Override
    public ReadOnlyBooleanProperty isSavingProperty() {
        return this.runningProperty();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                SerialListHolder<Receipt> dataInfo = localBusinessSupplier.get();
                receiptFileManager.updateDataSet(dataInfo);
                return null;
            }
        };
    }
}
