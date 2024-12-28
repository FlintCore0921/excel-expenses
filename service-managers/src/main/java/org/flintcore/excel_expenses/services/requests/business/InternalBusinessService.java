package org.flintcore.excel_expenses.services.requests.business;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.managers.services.pagination.IParameterizedPageRequest;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.dto.LocalBusinessDto;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessEndpointHolder;
import org.flintcore.excel_expenses.services.mappers.business.LocalBusinessListPageResponseMapperMapper;
import org.flintcore.excel_expenses.services.status.IFutureServerStatusService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Profile({"dev", "test", "internal-py-server"})
@Log4j2
public class InternalBusinessService
        extends InternalBusinessServiceImpl<IPageRequest, LocalBusiness>
        implements ILocalBusinessStatusService<LocalBusiness> {

    public InternalBusinessService(
            Supplier<IServerConnection> serverConnectionBuilder,
            @Qualifier("internalBusinessStatusService")
            IFutureServerStatusService statusService,
            RemoteRequestHelper requestHelper,
            InternalDGIIBusinessEndpointHolder endpointHolder) {
        super(serverConnectionBuilder, statusService, requestHelper, endpointHolder);
    }

    @Override
    public CompletableFuture<IPageListResponse<LocalBusiness>> queueResponse(IPageRequest pageRequest) {
        return this.<LocalBusinessDto>request(pageRequest, this.endpointHolder.getBusinessEndpointURI())
                .thenApply(new LocalBusinessListPageResponseMapperMapper());
    }

    @Override
    public CompletableFuture<IPageListResponse<LocalBusiness>> queueResponseByStatus(IParameterizedPageRequest pageRequest) {
        return this.<LocalBusinessDto>request(pageRequest, this.endpointHolder.getBusinessByStatusEndpointURI())
                .thenApply(new LocalBusinessListPageResponseMapperMapper());
    }
}
