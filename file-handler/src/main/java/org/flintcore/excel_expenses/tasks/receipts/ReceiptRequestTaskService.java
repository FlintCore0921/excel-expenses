package org.flintcore.excel_expenses.tasks.receipts;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptSerializeFileManager;
import org.flintcore.excel_expenses.files.receipts.ReceiptSerializeFileManager;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class ReceiptRequestTaskService extends ObservableService<List<Receipt>> {

    private final ReceiptSerializeFileManager<Receipt> receiptFileManager;

    public ReceiptRequestTaskService(PeriodReceiptSerializeFileManager receiptFileManager) {
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
    @PostConstruct
    protected void setupListeners() {
        super.setupListeners();
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

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
