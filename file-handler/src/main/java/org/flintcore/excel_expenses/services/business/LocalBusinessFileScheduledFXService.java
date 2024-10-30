package org.flintcore.excel_expenses.services.business;

import data.utils.NullableUtils;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.services.FileScheduledFxService;
import org.flintcore.excel_expenses.tasks.business.LocalBusinessRequestTaskService;
import org.flintcore.excel_expenses.tasks.business.LocalBusinessSaveTaskService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class LocalBusinessFileScheduledFXService extends FileScheduledFxService<LocalBusiness> {
    /**
     * Just the same as {@link #storeTaskService} but as original impl type.
     */
    private final LocalBusinessSaveTaskService localBusinessSaveTask;

    public LocalBusinessFileScheduledFXService(
            SubscriptionHolder subscriptionManager,
            LocalBusinessRequestTaskService localBusinessRequestTask,
            LocalBusinessSaveTaskService localBusinessSaveTask,
            ApplicationScheduler appScheduler
    ) {
        super(localBusinessRequestTask, localBusinessSaveTask, subscriptionManager, appScheduler);
        this.localBusinessSaveTask = localBusinessSaveTask;
    }

    public CompletableFuture<ObservableList<LocalBusiness>> getDataList() {
        NullableUtils.executeIsNull(this.dataSetList, this::requestData);
        return CompletableFuture.completedFuture(this.readOnlyListWrapper.getReadOnlyProperty());
    }

    protected void initObservableList() {
        if (Objects.nonNull(this.dataSetList)) return;

        this.dataSetList = FXCollections.observableSet();

        this.readOnlyListWrapper = new ReadOnlyListWrapper<>(this, null);
        this.readOnlyListWrapper.set(FXCollections.observableArrayList());

        this.localBusinessSaveTask.setLocalBusinessSupplier(
                () -> SerialListHolder.from(this.readOnlyListWrapper.getReadOnlyProperty())
        );

        setupOnLoadListeners();
    }

    protected void setupOnLoadListeners() {
        Subscription onSuccess = this.listenRequestTask(
                TaskFxEvent.WORKER_STATE_SUCCEEDED, () -> {
                    this.requiresRequest.set(false);
                    this.dataSetList.addAll(
                            this.requestTaskService.getValue()
                    );
                });

        this.subscriptionManager.appendSubscriptionOn(this.requestTaskService, onSuccess);
    }

    @Override
    protected void setupOnReadListeners() {
        super.setupOnReadListeners();

        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, () -> Platform.runLater(() -> {
            this.storeTaskService.restart();
            this.storeTaskService.getLastValue();
        }));
    }
}
