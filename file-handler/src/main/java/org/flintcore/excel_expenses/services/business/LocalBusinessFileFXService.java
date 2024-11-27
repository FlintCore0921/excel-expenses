package org.flintcore.excel_expenses.services.business;

import javafx.beans.property.ReadOnlyBooleanProperty;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxLoaderService;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxRepository;
import org.flintcore.excel_expenses.managers.services.business.IBusinessFxStorageService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Service to handle save/load data from file.
 */
@Service
@Log4j2
public class LocalBusinessFileFXService implements IBusinessFxRepository<LocalBusiness>,
        ISaveFxServiceStatus, ILoaderFxServiceStatus {

    private final ShutdownFXApplication shutdownHandler;
    private final IBusinessFxStorageService<LocalBusiness> localBusinessSaveService;
    private final IBusinessFxLoaderService<LocalBusiness> localBusinessRequestTask;

    public LocalBusinessFileFXService(
            ShutdownFXApplication shutdownHandler,
            LocalBusinessRequestFileService localBusinessRequestTask,
            LocalBusinessSaveFileService localBusinessSaveTask
    ) {
        this.shutdownHandler = shutdownHandler;
        this.localBusinessSaveService = localBusinessSaveTask;
        this.localBusinessRequestTask = localBusinessRequestTask;
    }

    @Override
    public Future<List<LocalBusiness>> getBusinessDataList() {
        return this.localBusinessRequestTask.getBusinessDataList();
    }

    @Override
    public Future<Void> saveData(LocalBusiness business) {
        return this.localBusinessSaveService.saveData(business);
    }

    @Override
    public Future<Void> saveData(List<LocalBusiness> business) {
        return this.localBusinessSaveService.saveData(business);
    }

    @Override
    public ReadOnlyBooleanProperty isRequestingProperty() {
        return this.localBusinessRequestTask.isRequestingProperty();
    }

    @Override
    public ReadOnlyBooleanProperty isSavingProperty() {
        return this.localBusinessSaveService.isSavingProperty();
    }

    protected void setupShutdownActions() {

    }
}
