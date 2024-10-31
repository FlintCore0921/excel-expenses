package org.flintcore.excel_expenses.services.business;

import data.utils.NullableUtils;
import javafx.collections.ObservableList;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.subscriptions.ShutdownSubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
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
            ApplicationScheduler appScheduler,
            ShutdownSubscriptionHolder shutDownSubscriptionHolder
    ) {
        super(localBusinessRequestTask, localBusinessSaveTask, subscriptionManager,
                appScheduler, shutDownSubscriptionHolder);
        this.localBusinessSaveTask = localBusinessSaveTask;
    }

    public CompletableFuture<ObservableList<LocalBusiness>> getDataList() {
        NullableUtils.executeIsNull(this.dataSetList, this::requestData);
        return CompletableFuture.completedFuture(this.readOnlyListWrapper.getReadOnlyProperty());
    }

    @Override
    protected void initObservableList() {
        super.initObservableList();

        if (Objects.isNull(this.readOnlyListWrapper)) return;

        this.localBusinessSaveTask.setLocalBusinessSupplier(
                () -> SerialListHolder.from(this.readOnlyListWrapper.getReadOnlyProperty())
        );
    }

    @Override
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

        // On Close application.
        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, () -> {
            if (Objects.isNull(this.dataSetList)) return;

            CompletableFuture<Boolean> onSaveHandler = new CompletableFuture<>();
            onSaveHandler.whenComplete((val, th) -> log.info("Data stored in the system."));

            this.storeTaskService.addOneTimeSubscription(TaskFxEvent.ALL_WORKER_STATE_DONE,
                    () -> onSaveHandler.complete(true)
            );

            this.storeTaskService.restart();
        });
    }
}
