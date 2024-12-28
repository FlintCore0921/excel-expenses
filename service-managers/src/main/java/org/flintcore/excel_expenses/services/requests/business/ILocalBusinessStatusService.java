package org.flintcore.excel_expenses.services.requests.business;

import org.flintcore.excel_expenses.managers.services.pagination.IParameterizedPageRequest;
import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;

import java.util.concurrent.CompletableFuture;

public interface ILocalBusinessStatusService<B extends IBusiness> {
    String STATUS_KEY = "serverStatus";
    CompletableFuture<IPageListResponse<B>> queueResponseByStatus(IParameterizedPageRequest pageRequest);
}
