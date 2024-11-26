package org.flintcore.excel_expenses.services;

import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Log4j2
public abstract class ScheduledFxService<T> extends ScheduledService<T> {
    protected final SubscriptionHolder subscriptionManager;
    @Lazy
    protected final ShutdownFXApplication shutDownHolder;

    protected ObservableSet<T> dataSetList;

    protected AtomicBoolean requiresRequest;

    public ScheduledFxService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder
    ) {
        this.subscriptionManager = subscriptionManager;
        this.shutDownHolder = shutDownHolder;
        this.requiresRequest = new AtomicBoolean(true);
    }

    public abstract ReadOnlyBooleanProperty requestingProperty();

    public abstract ReadOnlyBooleanProperty storingProperty();

    /**
     * Trigger the tasks to get the data.
     */
    public abstract void requestData();

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
     * Init on load tasks when triggers.
     */
    protected abstract void setupOnLoadListeners();

    /**
     * Trigger to listen when store requests
     */
    protected abstract void setupOnReadListeners();

    protected abstract void setupShutdownActions();

    protected void initObservableList() {
        if (Objects.nonNull(this.dataSetList)) return;

        if (!Platform.isFxApplicationThread())
            Platform.runLater(this::initFields);
        else this.initFields();

        setupOnLoadListeners();
        setupOnReadListeners();
    }

    @SuppressWarnings("unchecked")
    private void initFields() {
        this.dataSetList = FXCollections.observableSet();
    }

    @PreDestroy
    protected void onClose() {
        this.subscriptionManager.close();
    }
}
