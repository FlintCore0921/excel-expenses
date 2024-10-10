package org.flintcore.excel_expenses.services;

import org.flintcore.excel_expenses.handlers.receipts.ReceiptFileManager;
import org.flintcore.excel_expenses.models.expenses.Receipt;
import data.utils.NullableUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class ReceiptFileManagerService {
    private final Duration PERIOD_TO_UPDATE_DATA = Duration.ofMinutes(2L);
    private final Duration PERIOD_TO_FETCH_DATA = Duration.ofMinutes(10L);

    private final ReceiptFileManager receiptFileManager;
    private final Set<Receipt> receipts;
    private Timer timer;
    @Getter
    private boolean fetching;

    private TimerTask fetchTask;

    public ReceiptFileManagerService(ReceiptFileManager receiptFileManager) {
        this.receiptFileManager = receiptFileManager;
        this.receipts = new HashSet<>();
    }

    public void startRetrievingReceipts() {
        NullableUtils.executeIsNull(this.timer, () -> this.timer = new Timer());

        if (isFetching()) {
            return;
        }

        requestFetching(Duration.ZERO, PERIOD_TO_FETCH_DATA);
    }


    public void stopFetching() {
        this.fetching = false;
        NullableUtils.executeNonNull(this.fetchTask, TimerTask::cancel);
    }

    public void addNewReceipt(Receipt... receipt) {
        this.receipts.addAll(List.of(receipt));
    }

    public void deleteReceipt(Receipt... receipt) {
        this.receipts.removeAll(Set.of(receipt));
    }

    public void requestStoreData() {
        ArrayList<Receipt> receiptSave = new ArrayList<>(getReceipts());
        this.receiptFileManager.updateReceiptsData(receiptSave);
        this.stopFetching();
        this.requestFetching(PERIOD_TO_FETCH_DATA, PERIOD_TO_UPDATE_DATA);
    }

    public List<Receipt> getReceipts() {
        return new ArrayList<>(receipts);
    }

    private void requestFetching(Duration delay, Duration period) {
        this.fetching = true;

        fetchTask = new TimerTask() {
            @Override
            public void run() {
                // set state loading
                Set<Receipt> managerReceipts = receiptFileManager.getReceipts();
                receipts.clear();
                receipts.addAll(managerReceipts);
            }
        };
        this.timer.schedule(this.fetchTask, delay.toMillis(), period.toMillis());
    }

}
