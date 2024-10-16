package org.flintcore.excel_expenses.services.business;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.tasks.LocalBusinessRequestTaskService;
import org.flintcore.excel_expenses.tasks.LocalBusinessSaveTaskService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class LocalBusinessFileFXService {
    @Lazy
    private final LocalBusinessRequestTaskService localBusinessRequestTask;
    @Lazy
    private final LocalBusinessSaveTaskService localBusinessSaveTask;
    @Lazy
    private final ApplicationScheduler appScheduler;

    @Lazy
    private final SubscriptionHolder subscriptionManager;

    @Getter
    private ObservableList<LocalBusiness> localBusinessList;

    // For logging and messaging
    private LocalBusiness businessRunningMessage, businessFailedMessage;

    public Subscription listenRequestTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.localBusinessRequestTask.addSubscription(type, action);
    }

    public List<Subscription> listenRequestTask(List<EventType<WorkerStateEvent>> type, Runnable action) {
        return this.localBusinessRequestTask.addSubscription(type, action);
    }

    public Subscription listenStoreTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.localBusinessSaveTask.addSubscription(type, action);
    }

    public List<Subscription> listenStoreTask(List<EventType<WorkerStateEvent>> type, Runnable action) {
        return this.localBusinessSaveTask.addSubscription(type, action);
    }

    /**
     * Trigger the tasks to get the data.
     */
    public synchronized void requestData() {
        initBusinessObservable();

        if (!this.localBusinessRequestTask.isRunning()) {
            this.localBusinessRequestTask.restart();
        }
    }

    private void setupOnLoadListeners() {
        Subscription onRunning = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_RUNNING,
                () -> {
                    this.localBusinessList.clear();
                    this.localBusinessList.add(getOnRunningFlag());
                });

        Subscription onFail = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_FAILED,
                () -> {
                    this.localBusinessList.clear();
                    this.localBusinessList.add(getOnFailedFlag());
                });

        Subscription onSuccess = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                () -> {
                    this.localBusinessList.clear();
                    this.localBusinessList.addAll(
                            this.localBusinessRequestTask.getValue()
                    );
                });

        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onRunning);
        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onSuccess);
        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onFail);

        this.subscriptionManager.appendSubscriptionOn(
                this.localBusinessSaveTask,
                this.localBusinessRequestTask::cancel
        );
    }

    private void initBusinessObservable() {
        NullableUtils.executeIsNull(this.localBusinessList,
                () -> {
                    this.localBusinessList = FXCollections.observableArrayList();
                    this.localBusinessSaveTask.setLocalBusinessSupplier(
                            () -> new SerialListHolder<>(
                                    new ArrayList<>(this.localBusinessList)
                            )
                    );
                    setupOnLoadListeners();
                });
    }

    /**
     * Trigger the tasks to store current data and schedule it.
     */
    public synchronized void storeData() {
        TimerTask storeTask = new TimerTask() {
            final LocalBusinessSaveTaskService businessService = localBusinessSaveTask;

            @Override
            public void run() {
                if (!this.businessService.isRunning()) {
                    this.businessService.restart();
                }
            }
        };

        Subscription scheduled = this.appScheduler.schedule(storeTask, Duration.ofMinutes(10));

        this.subscriptionManager.appendSubscriptionOn(localBusinessSaveTask, scheduled);
    }

    private LocalBusiness getOnRunningFlag() {
        NullableUtils.executeIsNull(this.businessRunningMessage,
                () -> this.businessRunningMessage = new LocalBusiness("Loading", "Data..."));
        return this.businessRunningMessage;
    }

    private LocalBusiness getOnFailedFlag() {
        NullableUtils.executeIsNull(this.businessFailedMessage,
                () -> this.businessFailedMessage = new LocalBusiness("", "\rNo Data..."));
        return this.businessFailedMessage;
    }

    @PreDestroy
    private void onClose() {
        this.subscriptionManager.close();
    }
}
