package org.flintcore.excel_expenses.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
/**Service to handle CRUD operations in background FX threads.*/
@Log4j2
public abstract class FxService<T> extends Service<T> {
    @Lazy
    protected final SubscriptionHolder subscriptionManager;
    protected final ShutdownFXApplication shutDownHolder;
    protected ObservableSet<T> dataSetList;
    protected ObservableList<T> dataList;

    protected AtomicBoolean requiresRequest;
    protected AtomicBoolean wasStoreRequestStarted;

    // For logging and messaging

    @PostConstruct
    private void setAtomics() {
        this.requiresRequest = new AtomicBoolean(true);
        this.wasStoreRequestStarted = new AtomicBoolean(false);
    }

    public FxService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder
    ) {
        this.subscriptionManager = subscriptionManager;
        this.shutDownHolder = shutDownHolder;
    }

    public abstract ReadOnlyBooleanProperty requestingProperty();

    public abstract ReadOnlyBooleanProperty storingProperty();

    /**
     * Trigger the tasks to get the data.
     */
    public abstract void requestData();

    /**
     * Returns a Future with a **Read only** unmodifiable list
     */
    public abstract Future<ObservableList<T>> getDataList();

    protected abstract void initObservableList();

    public abstract CompletableFuture<Boolean> register(T business);

    public abstract CompletableFuture<Boolean> delete(T business);

    // On listen, subscriptions

    public abstract Subscription listenRequestTask(EventType<WorkerStateEvent> type, Runnable action);

    public abstract void listenRequestTaskOnce(EventType<WorkerStateEvent> type, Runnable action);

    public abstract Subscription listenRequestTask(List<EventType<WorkerStateEvent>> types, Runnable action);

    public abstract Subscription listenStoreTask(EventType<WorkerStateEvent> type, Runnable action);

    public abstract void listenStoreTaskOnce(EventType<WorkerStateEvent> type, Runnable action);

    public abstract void listenRequestTaskOnce(List<EventType<WorkerStateEvent>> types, Runnable action);

    public abstract void listenStoreTaskOnce(List<EventType<WorkerStateEvent>> types, Runnable action);

    public abstract Subscription listenStoreTask(List<EventType<WorkerStateEvent>> type, Runnable action);

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
        Platform.runLater(this.subscriptionManager::close);
    }
}
