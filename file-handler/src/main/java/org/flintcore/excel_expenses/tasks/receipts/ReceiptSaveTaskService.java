package org.flintcore.excel_expenses.tasks.receipts;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.Setter;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptFileManager;
import org.flintcore.excel_expenses.files.receipts.ReceiptFileManager;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class ReceiptSaveTaskService extends ObservableService<Void> {

    private final ReceiptFileManager<Receipt> receiptFileManager;

    @Setter
    private Supplier<SerialListHolder<Receipt>> localBusinessSupplier;

    public ReceiptSaveTaskService(PeriodReceiptFileManager receiptFileManager) {
        this.receiptFileManager = receiptFileManager;
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.events.get(type).remove(action);
        });
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                receiptFileManager.updateDataSet(
                        localBusinessSupplier.get()
                );
                return null;
            }
        };
    }


    @Override
    @PostConstruct
    protected void setupListeners() {
        super.setupListeners();
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
