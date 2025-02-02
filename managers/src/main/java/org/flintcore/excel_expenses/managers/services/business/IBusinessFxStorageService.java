package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.managers.services.ISaveFxServiceStatus;
import org.flintcore.excel_expenses.models.business.IBusiness;

public interface IBusinessFxStorageService<B extends IBusiness> extends IBusinessStorageService<B>,
        ISaveFxServiceStatus {
}
