package org.flintcore.excel_expenses.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableScheduledService;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableService;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Log4j2
public abstract class FileScheduledFxService<T> {
    @Lazy
    protected final ObservableService<List<T>> requestTaskService;
    @Lazy
    protected final ObservableScheduledService<Void> storeTaskService;
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ApplicationScheduler appScheduler;

    protected ObservableSet<T> dataSetList;
    protected ReadOnlyListWrapper<T> readOnlyListWrapper;

    protected AtomicBoolean requiresRequest;

    public FileScheduledFxService(
            ObservableService<List<T>> requestTaskService,
            ObservableScheduledService<Void> storeTaskService,
            SubscriptionHolder subscriptionManager,
            ApplicationScheduler appScheduler
    ) {
        this.requestTaskService = requestTaskService;
        this.storeTaskService = storeTaskService;
        this.subscriptionManager = subscriptionManager;
        this.appScheduler = appScheduler;
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
        if (!this.requestTaskService.isRunning() && requiresRequest.get()) {
            this.requestTaskService.restart();
        }
    }

    /**
     * Trigger the tasks to schedule store current data.
     */
    public void scheduleStoreData() {
        // Checks if value false, then set new value and proceed in case true.
        // otherwise ends method call.
        if (this.storeTaskService.getPeriod() != ObservableScheduledService.DEFAULT_RANGE_PERIOD) {
            this.storeTaskService.setPeriod(ObservableScheduledService.DEFAULT_RANGE_PERIOD);
        }
        this.storeTaskService.restart();
    }


    /**
     * Returns a Future with a **Read only** unmodifiable list
     */
    public abstract CompletableFuture<ObservableList<T>> getDataList();

    /**
     * Do not do anything here!
     */
    protected void setupOnLoadListeners() {
    }

    protected abstract void initObservableList();

    public abstract CompletableFuture<Boolean> register(T business);

    public abstract CompletableFuture<Boolean> delete(T business);

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
        return future.thenComposeAsync(
                __ -> CompletableFuture.completedFuture(response.get()),
                Platform::runLater
        );
    }

    @PreDestroy
    protected void onClose() {
        Platform.runLater(() -> {
            this.subscriptionManager.close();
            this.storeTaskService.restart();
        });
    }
}
