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
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableService;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class FileFxService<T> {
    @Lazy
    protected final ObservableService<List<T>> requestTaskService;
    @Lazy
    protected final ObservableService<Void> storeTaskService;
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ApplicationScheduler appScheduler;

    protected ObservableSet<T> dataSetList;
    protected ReadOnlyListWrapper<T> readOnlyListWrapper;

    protected AtomicBoolean requiresRequest;
    protected AtomicBoolean wasStoreRequestStarted;

    // For logging and messaging

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
        this.wasStoreRequestStarted = new AtomicBoolean(false);
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
        if (!this.wasStoreRequestStarted.compareAndSet(false, true)) return;

        TimerTask storeTask = new TimerTask() {

            @Override
            public void run() {
                if (!storeTaskService.isRunning()) {
                    // Update the request flag
                    requiresRequest.set(true);
                    storeTaskService.restart();
                }
            }
        };

        Subscription scheduled = this.appScheduler.schedule(storeTask, Duration.ofMinutes(10))
                .and(() -> this.wasStoreRequestStarted.set(false));

        this.subscriptionManager.appendSubscriptionOn(storeTaskService, scheduled);
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

        if (this.requiresRequest.get()) {
            this.requestData();
            this.listenRequestTaskOnce(TaskFxEvent.ALL_WORKER_STATE_DONE,
                    () -> futureResponse.complete(true)
            );
        }

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
        this.subscriptionManager.close();
        Platform.runLater(this.storeTaskService::restart);
    }
}
