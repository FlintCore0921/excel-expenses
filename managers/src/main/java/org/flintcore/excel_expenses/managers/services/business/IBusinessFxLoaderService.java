package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.models.expenses.IBusiness;

public interface IBusinessFxLoaderService<B extends IBusiness> extends IBusinessLoaderService<B>,
        ILoaderFxServiceStatus {
}