package org.flintcore.excel_expenses.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.subscriptions.ShutdownSubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.tasks.ObservableFXScheduledService;
import org.flintcore.excel_expenses.managers.subscriptions.tasks.ObservableFXService;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Log4j2
public abstract class FileScheduledFxService<T> {
    @Lazy
    protected final ObservableFXService<List<T>> requestTaskService;
    @Lazy
    protected final ObservableFXScheduledService<Void> storeTaskService;
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ApplicationScheduler appScheduler;
    @Lazy
    protected final ShutdownSubscriptionHolder shutDownHolder;

    protected ObservableSet<T> dataSetList;
    protected ReadOnlyListWrapper<T> readOnlyListWrapper;

    protected AtomicBoolean requiresRequest;

    public FileScheduledFxService(
            ObservableFXService<List<T>> requestTaskService,
            ObservableFXScheduledService<Void> storeTaskService,
            SubscriptionHolder subscriptionManager,
            ApplicationScheduler appScheduler, ShutdownSubscriptionHolder shutDownHolder
    ) {
        this.requestTaskService = requestTaskService;
        this.storeTaskService = storeTaskService;
        this.subscriptionManager = subscriptionManager;
        this.appScheduler = appScheduler;
        this.shutDownHolder = shutDownHolder;
    }

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
    }

    public ReadOnlyBooleanProperty requestingProperty() {
        return this.storeTaskService.runningProperty();
    }

    public ReadOnlyBooleanProperty storingProperty() {
        return this.requestTaskService.runningProperty();
    }

    /**
     * Trigger the tasks to get the data.
     */
    public void requestData() {
        initObservableList();
        Platform.runLater(() -> {
            if (!this.requestTaskService.isRunning() || requiresRequest.get()) {
                this.requestTaskService.restart();
            }
        });
    }

    /**
     * Trigger the tasks to schedule store current data.
     */
    public void requestStoreData() {
        // Checks if value false, then set new value and proceed in case true.
        // otherwise ends method call.
        this.storeTaskService.setPeriod(ObservableFXScheduledService.DEFAULT_RANGE_PERIOD);
        this.storeTaskService.restart();
    }


    /**
     * Returns a Future with a **Read only** unmodifiable list
     */
    public abstract CompletableFuture<ObservableList<T>> getDataList();

    public CompletableFuture<Boolean> register(T item) {
//      TODO  Test do not fail on not related FX thread
        return composeWith(
                prepareBooleanFutureForRequest(),
                () -> this.dataSetList.add(item)
        );
    }

    public CompletableFuture<Boolean> delete(T item) {
//      TODO  Test do not fail on not related FX thread
        return composeWith(
                prepareBooleanFutureForRequest(),
                () -> this.dataSetList.remove(item)
        );
    }

    // On listen, subscriptions

    public Subscription listenRequestTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.requestTaskService.addSubscription(type, action);
    }

    public void listenRequestTaskOnce(EventType<WorkerStateEvent> type, Runnable action) {
        this.requestTaskService.addOneTimeSubscription(type, action);
    }

    public Subscription listenRequestTask(List<EventType<WorkerStateEvent>> types, Runnable action) {
        return this.requestTaskService.addSubscription(types, action);
    }

    public Subscription listenStoreTask(EventType<WorkerStateEvent> type, Runnable action) {
        return this.storeTaskService.addSubscription(type, action);
    }

    public void listenStoreTaskOnce(EventType<WorkerStateEvent> type, Runnable action) {
        this.storeTaskService.addOneTimeSubscription(type, action);
    }

    public void listenRequestTaskOnce(List<EventType<WorkerStateEvent>> types, Runnable action) {
        this.requestTaskService.addOneTimeSubscription(types, action);
    }

    public void listenStoreTaskOnce(List<EventType<WorkerStateEvent>> types, Runnable action) {
        this.storeTaskService.addOneTimeSubscription(types, action);

    }

    public Subscription listenStoreTask(List<EventType<WorkerStateEvent>> type, Runnable action) {
        return this.storeTaskService.addSubscription(type, action);
    }

    // Utils
    protected CompletableFuture<Boolean> prepareBooleanFutureForRequest() {
        CompletableFuture<Boolean> futureResponse = new CompletableFuture<>();

        if (!this.requiresRequest.get()) {
            futureResponse.complete(true);
        } else {
            this.listenRequestTaskOnce(TaskFxEvent.ALL_WORKER_STATE_DONE,
                    () -> futureResponse.complete(true)
            );
        }
        this.requestData();

        return futureResponse;
    }

    protected CompletableFuture<Boolean> composeWith(
            CompletableFuture<Boolean> future, Supplier<Boolean> response
    ) {
        return composeWith(future, response, Platform::runLater);
    }

    protected CompletableFuture<Boolean> composeWith(
            CompletableFuture<Boolean> future, Supplier<Boolean> response, Executor exc
    ) {
        return future.thenComposeAsync(
                __ -> CompletableFuture.completedFuture(response.get()),
                exc
        );
    }

    /**
     * Do not do anything here!
     */
    protected void setupOnLoadListeners() {
    }

    /**
     * Trigger to listen when
     */
    protected void setupOnReadListeners() {
        Subscription onSucceeded = this.listenStoreTask(TaskFxEvent.WORKER_STATE_SUCCEEDED,
                () -> this.requiresRequest.set(true));

        this.subscriptionManager.appendSubscriptionOn(this.storeTaskService, onSucceeded);

        // Shutdown tasks

        this.shutDownHolder.addSubscription(this.storeTaskService, () -> {
            CountDownLatch lack = new CountDownLatch(1);
            try {
                this.storeTaskService.addOneTimeSubscription(TaskFxEvent.ALL_WORKER_STATE_DONE,
                        lack::countDown
                );

                Platform.runLater(this.storeTaskService::restart);

                lack.await();
                log.info("Data stored from {} service", getClass().getSimpleName());
            } catch (InterruptedException ignored) {
                log.info("Fail storing data...");
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void initObservableList() {
        if (Objects.nonNull(this.dataSetList)) return;

        this.dataSetList = FXCollections.observableSet();

        this.readOnlyListWrapper = new ReadOnlyListWrapper<>(this, null);
        this.readOnlyListWrapper.set(FXCollections.observableArrayList());

        appendListenerOnReaderProperty();

        setupOnLoadListeners();
        setupOnReadListeners();
    }

    // Read data from main list to wrapper.
    protected void appendListenerOnReaderProperty() {
        this.dataSetList.addListener((SetChangeListener<? super T>) change -> {
            if (change.wasAdded()) {
                this.readOnlyListWrapper.add(change.getElementAdded());
            }

            if (change.wasRemoved()) {
                this.readOnlyListWrapper.add(change.getElementRemoved());
            }
        });
    }

    @PreDestroy
    protected void onClose() {
        this.subscriptionManager.close();
    }
}