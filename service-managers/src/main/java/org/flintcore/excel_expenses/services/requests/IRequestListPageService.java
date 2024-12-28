package org.flintcore.excel_expenses.services.requests;

import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;

import java.io.Closeable;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

public interface IRequestListPageService<P extends IPageRequest, T extends Serializable> extends Closeable {
    CompletableFuture<IPageListResponse<T>> queueResponse(P pageRequest);
}
