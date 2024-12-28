package org.flintcore.excel_expenses.configurations.business;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxLoaderService;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxRepository;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxStorageService;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;
import org.flintcore.utilities.lists.ObservableListUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to handle save/load data from file.
 */
@Service
@Log4j2
public class LocalBusinessFileFXService implements IBusinessFxRepository<LocalBusiness>,
        ISaveFxServiceStatus, ILoaderFxServiceStatus {
    // Values to wait before save data
    private static final int SAVE_COUNT_GAP = 7;

    private final IBusinessFxStorageService<LocalBusiness> localBusinessSaveService;
    private final IBusinessFxLoaderService<LocalBusiness> localBusinessRequestTask;

    private final AtomicInteger saveCounter;

    protected ObservableSet<LocalBusiness> localBusinesses;

    public LocalBusinessFileFXService(
            LocalBusinessRequestFileService localBusinessRequestTask,
            LocalBusinessSaveFileService localBusinessSaveTask
    ) {
        this.localBusinessSaveService = localBusinessSaveTask;
        this.localBusinessRequestTask = localBusinessRequestTask;
        saveCounter = new AtomicInteger(SAVE_COUNT_GAP);
    }

    @Override
    public Future<ObservableList<LocalBusiness>> getBusinessDataList() {
        this.initDataSet();

        return CompletableFuture.supplyAsync(() -> {
            ObservableList<LocalBusiness> businessDataList = FXCollections.observableArrayList();
            ObservableListUtils.listenList(this.localBusinesses, businessDataList);
            return businessDataList;
        });
    }

    public Future<Void> saveData(@NonNull LocalBusiness business) {
        return saveData(List.of(business));
    }


    @Override
    public Future<Void> saveData(List<LocalBusiness> business) {
        this.initDataSet();
        int computedCounter = Math.max(this.saveCounter.get() - business.size(), 0);

        this.saveCounter.set(computedCounter);

        try {
            var saved = this.localBusinesses.addAll(business);

            if (!saved) return CompletableFuture.failedFuture(
                    new RuntimeException("Business already exists.")
            );

            if (this.saveCounter.compareAndSet(0, SAVE_COUNT_GAP)) {
                ObservableList<LocalBusiness> savedData = FXCollections.observableArrayList();
                savedData.addAll(this.localBusinesses);
                return this.localBusinessSaveService.saveData(savedData);
            }

            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(new RuntimeException());
        }
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.localBusinessRequestTask.isRequestingProperty();
    }

    @Override
    public ReadOnlyBooleanProperty isSavingProperty() {
        return this.localBusinessSaveService.isSavingProperty();
    }

    protected void initDataSet() {
        if (localBusinesses != null) return;
        this.localBusinesses = FXCollections.observableSet();

        FutureHandlerUtils.asCompletable(
                this.localBusinessRequestTask.getBusinessDataList()
        ).thenApply(this.localBusinesses::addAll);
    }
}
