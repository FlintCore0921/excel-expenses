package org.flintcore.excel_expenses.services.requests;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.models.requests.DefaultPageListResponse;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.exceptions.ServerConnectionException;
import org.flintcore.excel_expenses.services.internal.endpoints.IRequestEndpointHolder;
import org.flintcore.excel_expenses.services.status.IFutureServerStatusService;
import org.flintcore.excel_expenses.services.utils.PageRequestUtils;
import org.flintcore.excel_expenses.services.utils.RequestParamUtils;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.flintcore.excel_expenses.services.requests.IRequestResponses.EMPTY_LIST_PAGE_RESPONSE;

@RequiredArgsConstructor
public abstract class RequestListPageServiceImpl<P extends IPageRequest,
        T extends Serializable, U extends IRequestEndpointHolder,
        RR extends RemoteRequestHelper,
        US extends IFutureServerStatusService
        > implements IRequestListPageService<P, T> {

    protected final Supplier<IServerConnection> serverConnectionBuilder;
    protected final US statusService;
    protected final RR requestHelper;
    protected final U endpointHolder;

    protected final AtomicReference<IServerConnection> connection;

    public RequestListPageServiceImpl(
            Supplier<IServerConnection> serverConnectionBuilder,
            US statusService,
            RR requestHelper,
            U endpointHolder) {
        this(serverConnectionBuilder, statusService, requestHelper, endpointHolder,
                new AtomicReference<>());
    }


    protected <R> CompletableFuture<IPageListResponse<R>> request(
            final IPageRequest pageRequest, final URI URI
    ) {
        IServerConnection currentServerConnection = this.connection.updateAndGet(
                conn -> ObjectUtils
                        .defaultIfNull(conn, serverConnectionBuilder.get())
        );

        if (!currentServerConnection.isClosed()) return CompletableFuture.failedFuture(
                new ServerConnectionException()
        );

        Future<IPageListResponse<R>> submit = Holder.EXECUTOR_SERVICE.submit(() -> {
            // Wait for server is connected.
            currentServerConnection.waitUntil(IServerConnection.State.CONNECTED)
                    .get();

            // Get server serverStatus.
            if (!this.statusService.isUp())
                throw new ServerConnectionException();

            var requestResponse = this.requestHelper.getRequestFrom(
                    URI,
                    RequestParamUtils.<DefaultPageListResponse<R>>buildRequestParameter(),
                    PageRequestUtils.asParams(pageRequest)
            );

            // Checks is throws exception or may be null
            if (!requestResponse.getStatusCode().is2xxSuccessful())
                return (IPageListResponse<R>) EMPTY_LIST_PAGE_RESPONSE;

            return requestResponse.getBody();
        });

        return FutureHandlerUtils.asCompletable(submit);
    }

    @Override
    public void close() throws IOException {
        IServerConnection serverConnection = this.connection.get();
        if (Objects.isNull(serverConnection) || serverConnection.isClosed()) return;
        serverConnection.close();
    }

    protected static final class Holder {
        public static final ExecutorService EXECUTOR_SERVICE =
                Executors.newFixedThreadPool(2);
    }
}
