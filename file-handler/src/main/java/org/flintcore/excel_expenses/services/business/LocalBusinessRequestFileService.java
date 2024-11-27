package org.flintcore.excel_expenses.services.business;

import jakarta.annotation.PreDestroy;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxLoaderService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.services.FxListService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@Log4j2
public class LocalBusinessRequestFileService extends FxListService<LocalBusiness>
        implements IBusinessFxLoaderService<LocalBusiness> {

    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    public LocalBusinessRequestFileService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder,
            FXRunnableEventHandler eventHandler,
            LocalBusinessSerializeFileManager localBusinessFileManager
    ) {
        super(subscriptionManager, shutDownHolder, eventHandler);
        this.localBusinessFileManager = localBusinessFileManager;
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.runningProperty();
    }

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        var future = new CompletableFuture<List<LocalBusiness>>();

        this.eventHandler.addOneTimeSubscription(TaskFxEvent.WORKER_STATE_SUCCEEDED, () -> {
            future.complete(this.getValue());
        });

        this.eventHandler.addOneTimeSubscription(TaskFxEvent.WORKER_STATE_FAILED, () -> {
            future.complete(List.of());
        });

        this.restart();

        return future;
    }

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        return this.eventHandler.addSubscription(type, action);
    }

    @Override
    protected Task<List<LocalBusiness>> createTask() {
        return new Task<>() {
            @Override
            protected List<LocalBusiness> call() /*throws Exception*/ {
                return localBusinessFileManager.getDataList();
            }
        };
    }

    @PreDestroy
    public void close() {
        this.subscriptionManager.close();
    }
}
