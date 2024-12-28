package org.flintcore.excel_expenses.services.internal.status;

import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.services.FxService;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.internal.endpoints.IStatusEndpointHolder;
import org.flintcore.excel_expenses.services.models.ServerStatusResponse;
import org.flintcore.excel_expenses.services.status.IFutureServerStatusService;
import org.flintcore.excel_expenses.services.utils.RequestParamUtils;
import org.flintcore.utilities.runnables.RunnableThrowableOmitted;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Request data from internal server.
 */
@Log4j2
public final class InternalBusinessStatusFxService extends FxService<ServerStatusResponse>
        implements IFutureServerStatusService {

    private final RemoteRequestHelper requestHelper;
    private final IStatusEndpointHolder endpointHolder;

    public InternalBusinessStatusFxService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutdownHandler,
            RemoteRequestHelper requestHelper,
            IStatusEndpointHolder endpointHolder
    ) {
        super(eventHandler, shutdownHandler);
        this.requestHelper = requestHelper;
        this.endpointHolder = endpointHolder;
    }

    @Override
    protected Task<ServerStatusResponse> createTask() {
        return new Task<>() {
            @Override
            protected ServerStatusResponse call() {
                try {
                    ResponseEntity<ServerStatusResponse> responseEntity = requestHelper.getRequestFrom(
                            endpointHolder.getStatusEndpointURI(),
                            RequestParamUtils.buildRequestParameter()
                    );

                    ServerStatusResponse statusResponse = Objects.requireNonNull(responseEntity.getBody());

                    if (!responseEntity.getStatusCode().is2xxSuccessful()
                            || statusResponse.status()) return DEFAULT_SERVER_STATUS_RESPONSE;

                    return statusResponse;
                } catch (Exception e) {
                    log.error("Error requesting state: ", e);
                }

                return DEFAULT_SERVER_STATUS_RESPONSE;
            }
        };
    }

    @Override
    public Future<ServerStatusResponse> getStatus() {
        ServerStatusResponse statusResponse = this.getValue();

        var response = new CompletableFuture<ServerStatusResponse>();

        if (!this.isRunning() && Objects.isNull(statusResponse)
                || Objects.equals(statusResponse, DEFAULT_SERVER_STATUS_RESPONSE)) {
            Platform.runLater(this::restart);

            this.eventHandler.handle(TaskFxEvent.ALL_WORKER_STATE_DONE,
                    RunnableThrowableOmitted.asRunnable(() -> response.complete(this.getValue()))
            );

            return response;
        }

        response.complete(this.getValue());

        return response;
    }

    @Override
    public boolean isUp() {
        return !Objects.equals(this.valueProperty().getValue(), DEFAULT_SERVER_STATUS_RESPONSE);
    }

}
