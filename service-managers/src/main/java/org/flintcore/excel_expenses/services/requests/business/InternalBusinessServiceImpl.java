package org.flintcore.excel_expenses.services.requests.business;

import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.internal.endpoints.IBusinessEndpointHolder;
import org.flintcore.excel_expenses.services.requests.RequestListPageServiceImpl;
import org.flintcore.excel_expenses.services.status.IFutureServerStatusService;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public abstract class InternalBusinessServiceImpl<P extends IPageRequest, B extends IBusiness>
        extends RequestListPageServiceImpl<P, B, IBusinessEndpointHolder,
        RemoteRequestHelper, IFutureServerStatusService> {
    public InternalBusinessServiceImpl(
            Supplier<IServerConnection> serverConnectionBuilder,
            IFutureServerStatusService statusService,
            RemoteRequestHelper requestHelper,
            IBusinessEndpointHolder endpointHolder,
            AtomicReference<IServerConnection> connection) {
        super(serverConnectionBuilder, statusService, requestHelper, endpointHolder, connection);
    }

    public InternalBusinessServiceImpl(
            Supplier<IServerConnection> serverConnectionBuilder,
            IFutureServerStatusService statusService,
            RemoteRequestHelper requestHelper,
            IBusinessEndpointHolder endpointHolder) {
        super(serverConnectionBuilder, statusService, requestHelper, endpointHolder);
    }
}
