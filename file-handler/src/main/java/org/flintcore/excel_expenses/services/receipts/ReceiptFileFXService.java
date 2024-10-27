package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.services.FileFxService;
import org.flintcore.excel_expenses.tasks.receipts.ReceiptRequestTaskService;
import org.flintcore.excel_expenses.tasks.receipts.ReceiptSaveTaskService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Log4j2
public class ReceiptFileFXService extends FileFxService<Receipt> {
    private final ReceiptSaveTaskService saveTaskService;

    public ReceiptFileFXService(
            ReceiptSaveTaskService saveTaskService,
            ReceiptRequestTaskService requestTaskService,
            SubscriptionHolder subscriptionManager,
            ApplicationScheduler appScheduler
    ) {
        super(requestTaskService, saveTaskService, subscriptionManager, appScheduler);
        this.saveTaskService = saveTaskService;
    }

    protected void initObservableList() {
        NullableUtils.executeIsNull(this.dataSetList, () -> {
            this.dataSetList = FXCollections.observableSet();

            this.readOnlyListWrapper = new ReadOnlyListWrapper<>(this, null);
            this.readOnlyListWrapper.set(FXCollections.observableArrayList());

            appendListenerOnReaderProperty();

            this.saveTaskService.setLocalBusinessSupplier(
                    () -> SerialListHolder.from(this.dataSetList)
            );
        });
    }

    private void appendListenerOnReaderProperty() {
        this.dataSetList.addListener((SetChangeListener<? super Receipt>) change -> {
            if (change.wasAdded()) {
                this.readOnlyListWrapper.add(change.getElementAdded());
            }

            if (change.wasRemoved()) {
                this.readOnlyListWrapper.add(change.getElementRemoved());
            }
        });
    }

    @Override
    public CompletableFuture<ObservableList<Receipt>> getDataList() {
        NullableUtils.executeIsNull(this.dataSetList, this::requestData);
        return CompletableFuture.completedFuture(
                readOnlyListWrapper.getReadOnlyProperty()
        );
    }

    @Override
    public CompletableFuture<Boolean> register(Receipt item) {
        return composeWith(prepareBooleanFutureForRequest(),
                () -> this.dataSetList.add(item)
        );
    }

    @Override
    public CompletableFuture<Boolean> delete(Receipt item) {
        return composeWith(prepareBooleanFutureForRequest(),
                () -> this.dataSetList.remove(item)
        );
    }
}