package org.flintcore.excel_expenses.services.business;

import jakarta.annotation.PreDestroy;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxStorageService;
import org.flintcore.excel_expenses.managers.services.business.IBusinessStorageService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.subscriptions.events.FXRunnableEventHandler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.services.ScheduledFxService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Log4j2
public class LocalBusinessSaveFileService extends ScheduledFxService<Void>
        implements IBusinessFxStorageService<LocalBusiness> {

    public static final long SECONDS_CLOSE_TIMEOUT = 20L;
    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    private AtomicReference<SerialListHolder<LocalBusiness>> localBusinessSupplier;

    public LocalBusinessSaveFileService(
            SubscriptionHolder subscriptionManager,
            ShutdownFXApplication shutDownHolder,
            FXRunnableEventHandler eventHandler,
            LocalBusinessSerializeFileManager localBusinessFileManager
    ) {
        super(subscriptionManager, shutDownHolder, eventHandler);
        this.localBusinessFileManager = localBusinessFileManager;
    }

    @Override
    public Future<Void> saveData(@NonNull LocalBusiness business) {
        return saveData(List.of(business));
    }

    @Override
    public Future<Void> saveData(List<LocalBusiness> business) {
        if (Objects.isNull(this.localBusinessSupplier))
            this.localBusinessSupplier = new AtomicReference<>();

        this.localBusinessSupplier.set(new SerialListHolder<>(business));

        var future = new CompletableFuture<Void>();

        this.eventHandler.addOneTimeSubscription(TaskFxEvent.WORKER_STATE_SUCCEEDED,
                () -> future.complete(null)
        );

        this.forceStart();

        return future;
    }

    @Override
    public ReadOnlyBooleanProperty isSavingProperty() {
        return this.runningProperty();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                SerialListHolder<LocalBusiness> data = localBusinessSupplier
                        .getAndSet(null);
                if (data == null) {
                    return null;
                }
                localBusinessFileManager.updateDataSet(data);
                return null;
            }
        };
    }

    @Override
    protected void setupShutdownActions() {
        this.shutDownHolder.addSubscription(this, () -> {
            var lastTaskValue = this.localBusinessSupplier.get();

            if (isRunning() || Objects.isNull(lastTaskValue)) return;

            try {
                var countDown = new CountDownLatch(1);

                this.eventHandler.addOneTimeSubscription(
                        TaskFxEvent.ALL_WORKER_STATE_DONE,
                        countDown::countDown
                );
                this.forceStart();
                // Wait
                countDown.await(SECONDS_CLOSE_TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
        });
    }

    @PreDestroy
    public void close() throws Exception {
        this.eventHandler.close();
    }
}
