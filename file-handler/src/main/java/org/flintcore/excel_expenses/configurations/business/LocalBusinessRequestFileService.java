package org.flintcore.excel_expenses.configurations.business;

import jakarta.annotation.PreDestroy;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.managers.services.FxListService;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxLoaderService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@Log4j2
public class LocalBusinessRequestFileService extends FxListService<LocalBusiness>
        implements IBusinessFxLoaderService<LocalBusiness> {

    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    public LocalBusinessRequestFileService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutDownHolder,
            LocalBusinessSerializeFileManager localBusinessFileManager
    ) {
        super(eventHandler, shutDownHolder);
        this.localBusinessFileManager = localBusinessFileManager;
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.runningProperty();
    }

    @Override
    public Future<ObservableList<LocalBusiness>> getBusinessDataList() {
        var future = new CompletableFuture<ObservableList<LocalBusiness>>();

        this.eventHandler.handleOnce(TaskFxEvent.WORKER_STATE_SUCCEEDED, () -> {
            future.complete(this.getValue());
        });

        this.eventHandler.handleOnce(TaskFxEvent.WORKER_STATE_FAILED, () -> {
            future.complete(FXCollections.observableArrayList());
        });

        this.restart();

        return future;
    }

    @Override
    protected Task<ObservableList<LocalBusiness>> createTask() {
        return new Task<>() {
            @Override
            protected ObservableList<LocalBusiness> call() /*throws Exception*/ {
                return FXCollections.observableArrayList(localBusinessFileManager.getDataList());
            }
        };
    }

    @PreDestroy
    public void close() {
        this.shutDownHolder.close();
    }
}
