package org.flintcore.excel_expenses.services.requests;

import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.models.requests.IPageResponse;

import java.io.Serializable;
import java.util.concurrent.Future;

public interface IRequestPageService<P extends IPageRequest, T extends Serializable> {
    Future<IPageResponse<T>> queueResponse(P pageRequest);
}
