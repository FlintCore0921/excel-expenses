package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;

import java.util.concurrent.Future;

public interface IBusinessPaginationFxService<B extends IBusiness>
        extends IBusinessFxLoaderService<B>, ILoaderFxServiceStatus {
    Future<IPageListResponse<B>> getBusinessDataList(IPageRequest page);
}
