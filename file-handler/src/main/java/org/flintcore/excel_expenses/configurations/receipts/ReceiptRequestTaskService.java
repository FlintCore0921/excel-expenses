package org.flintcore.excel_expenses.configurations.receipts;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptSerializeFileManager;
import org.flintcore.excel_expenses.files.receipts.ReceiptSerializeFileManager;
import org.flintcore.excel_expenses.managers.services.FxService;
import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IEventSubscriptionHandler;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ReceiptRequestTaskService extends FxService<List<Receipt>>
        implements ILoaderFxServiceStatus, IEventSubscriptionHandler<WorkerStateEvent, Runnable> {

    private final ReceiptSerializeFileManager<Receipt> receiptFileManager;

    public ReceiptRequestTaskService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutdownFXApplication,
            PeriodReceiptSerializeFileManager receiptFileManager) {
        super(eventHandler, shutdownFXApplication);
        this.receiptFileManager = receiptFileManager;
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.runningProperty();
    }

    @Override
    protected Task<List<Receipt>> createTask() {
        return new Task<>() {
            @Override
            protected List<Receipt> call() /*throws Exception*/ {
                return receiptFileManager.getDataList();
            }
        };
    }
}
