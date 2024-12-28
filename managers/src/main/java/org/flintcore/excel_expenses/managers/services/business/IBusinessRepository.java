package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.models.business.IBusiness;

public interface IBusinessRepository<B extends IBusiness>
        extends IBusinessStorageService<B>, IBusinessLoaderService<B> {
}
