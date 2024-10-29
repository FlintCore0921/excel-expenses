package org.flintcore.excel_expenses.services.business;

import data.utils.NullableUtils;
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
    /** Just the same as {@link #storeTaskService} but as original impl type. */
    private final LocalBusinessSaveTaskService localBusinessSaveTask;

    // For logging and messaging
    private LocalBusiness businessRunningMessage, businessFailedMessage;

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

    /**
     * Append a new {@link LocalBusiness} into the service.
     *
     * @return true is data was added and was not a duplicate. If {@link #requestData()} method has not
     * been called, so returns false.
     */
    public CompletableFuture<Boolean> register(LocalBusiness business) {
        return composeWith(prepareBooleanFutureForRequest(),
                () -> this.dataSetList.add(business)
        );
    }

    // TODO
    @Override
    public CompletableFuture<Boolean> delete(LocalBusiness business) {
        return composeWith(prepareBooleanFutureForRequest(),
                () -> this.dataSetList.remove(business)
        );
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
        // Debug
        this.subscriptionManager.appendSubscriptionOn(this,
                this.listenRequestTask(
                        TaskFxEvent.WORKER_STATE_READY,
                        () -> log.info("ON Ready called!")
                )
        );

        Subscription onReady = this.listenRequestTask(
                TaskFxEvent.WORKER_STATE_RUNNING,
                () -> {
                    this.dataSetList.clear();
                    this.dataSetList.add(getOnRunningFlag());
                });

        Subscription onSuccess = this.listenRequestTask(
                TaskFxEvent.WORKER_STATE_SUCCEEDED,
                () -> {
                    this.requiresRequest.set(false);
                    this.dataSetList.clear();
                    this.dataSetList.addAll(
                            this.requestTaskService.getValue()
                    );
                });

        Subscription onFail = this.listenRequestTask(
                TaskFxEvent.WORKER_STATE_FAILED,
                () -> {
                    this.dataSetList.clear();
                    this.dataSetList.add(getOnFailedFlag());
                });


        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, onReady);
        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, onSuccess);
        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, onFail);

        this.subscriptionManager.appendSubscriptionOn(
                this.storeTaskService,
                this.requestTaskService::cancel
        );
    }

    protected LocalBusiness getOnRunningFlag() {
        NullableUtils.executeIsNull(this.businessRunningMessage,
                () -> this.businessRunningMessage = new LocalBusiness("Loading", "Data..."));
        return this.businessRunningMessage;
    }

    protected LocalBusiness getOnFailedFlag() {
        NullableUtils.executeIsNull(this.businessFailedMessage,
                () -> this.businessFailedMessage = new LocalBusiness("", "\rNo Data..."));
        return this.businessFailedMessage;
    }
}
