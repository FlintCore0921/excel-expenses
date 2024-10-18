package org.flintcore.excel_expenses.services.business;

import data.utils.NullableUtils;
import events.TaskFxEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
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
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean requiresRequest;

    private ObservableList<LocalBusiness> localBusinessList;

    // For logging and messaging
    private LocalBusiness businessRunningMessage, businessFailedMessage;

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
    }

    public boolean isRequestingData() {
        return this.localBusinessRequestTask.isRunning();
    }

    public boolean isStoringData() {
        return this.localBusinessSaveTask.isRunning();
    }

    public CompletableFuture<ObservableList<LocalBusiness>> getLocalBusinessList() {
        NullableUtils.executeIsNull(this.localBusinessList, this::requestData);
        return CompletableFuture.completedFuture(this.localBusinessList);
    }

    public Subscription listenRequestTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.localBusinessRequestTask.addSubscription(type, action);
    }

    public Subscription listenRequestTask(List<EventType<WorkerStateEvent>> type, Runnable action) {
        return this.localBusinessRequestTask.addSubscription(type, action);
    }

    public Subscription listenStoreTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.localBusinessSaveTask.addSubscription(type, action);
    }

    public Subscription listenStoreTask(List<EventType<WorkerStateEvent>> type, Runnable action) {
        return this.localBusinessSaveTask.addSubscription(type, action);
    }

    /**
     * Append a new {@link LocalBusiness} into the service.
     *
     * @return true is data was added and was not a duplicate. If {@link #requestData()} method has not
     * been called, so returns false.
     */
    public CompletableFuture<Boolean> registerBusiness(LocalBusiness business) {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        if (this.requiresRequest.get()) {
            this.requestData();
            this.listenRequestTask(TaskFxEvent.WORKER_STATE_DONE,
                    () -> futureResponse.complete(true)
            );
        }

        return futureResponse.thenCompose(result -> {
            CompletableFuture<Boolean> addBusinessFuture = new CompletableFuture<>();
            Platform.runLater(() -> addBusinessFuture.complete(
                    this.localBusinessList.add(business)
            ));
            return addBusinessFuture;
        });
    }

    /**
     * Trigger the tasks to get the data.
     */
    public synchronized void requestData() {
        initBusinessObservable();
        if (!this.localBusinessRequestTask.isRunning() && requiresRequest.get()) {
            this.localBusinessRequestTask.restart();
        }
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
                    // Update the request flag
                    requiresRequest.set(true);
                    this.businessService.restart();
                }
            }
        };

        Subscription scheduled = this.appScheduler.schedule(storeTask, Duration.ofMinutes(10));

        this.subscriptionManager.appendSubscriptionOn(localBusinessSaveTask, scheduled);
    }


    private void initBusinessObservable() {
        if (Objects.nonNull(this.localBusinessList)) return;

        this.localBusinessList = FXCollections.observableArrayList();
        this.localBusinessSaveTask.setLocalBusinessSupplier(
                () -> new SerialListHolder<>(
                        new ArrayList<>(this.localBusinessList)
                )
        );
        setupOnLoadListeners();
    }

    private void setupOnLoadListeners() {
        Subscription onReady = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_RUNNING,
                () -> {
                    this.localBusinessList.clear();
                    this.localBusinessList.add(getOnRunningFlag());
                });

        Subscription onSuccess = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                () -> {
                    this.requiresRequest.set(false);
                    this.localBusinessList.clear();
                    this.localBusinessList.addAll(
                            this.localBusinessRequestTask.getValue()
                    );
                });

        Subscription onFail = this.listenRequestTask(
                WorkerStateEvent.WORKER_STATE_FAILED,
                () -> {
                    this.localBusinessList.clear();
                    this.localBusinessList.add(getOnFailedFlag());
                });


        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onReady);
        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onSuccess);
        this.subscriptionManager.appendSubscriptionOn(this.localBusinessSaveTask, onFail);

        this.subscriptionManager.appendSubscriptionOn(
                this.localBusinessSaveTask,
                this.localBusinessRequestTask::cancel
        );
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
        Platform.runLater(this.localBusinessSaveTask::restart);
    }
}
