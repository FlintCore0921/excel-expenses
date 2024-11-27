package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptSerializeFileManager;
import org.flintcore.excel_expenses.files.receipts.ReceiptSerializeFileManager;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Log4j2
@Component
public class ReceiptSaveTaskService extends ObservableFXScheduledService<Void> {

    private final ReceiptSerializeFileManager<Receipt> receiptFileManager;

    @Setter
    private Supplier<SerialListHolder<Receipt>> localBusinessSupplier;

    public ReceiptSaveTaskService(PeriodReceiptSerializeFileManager receiptFileManager) {
        this.receiptFileManager = receiptFileManager;
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.getEventListenerHolder().get(type).remove(action);
        });
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

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
