package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.models.expenses.IBusiness;

public interface IBusinessFxRepository<B extends IBusiness>
        extends IBusinessFxStorageService<B>, IBusinessFxLoaderService<B> {
}
