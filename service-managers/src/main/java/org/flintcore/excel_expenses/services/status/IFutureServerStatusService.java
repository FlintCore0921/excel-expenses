package org.flintcore.excel_expenses.services.status;

import org.flintcore.excel_expenses.services.models.ServerStatusResponse;

import java.util.Objects;

public interface IFutureServerStatusService
        extends IFutureStatusService<ServerStatusResponse> {
    ServerStatusResponse DEFAULT_SERVER_STATUS_RESPONSE = new ServerStatusResponse(false);

    /**
     * This checks if service response is valid.
     */
    static boolean isServiceUp(ServerStatusResponse serviceResponse) {
        return Objects.equals(DEFAULT_SERVER_STATUS_RESPONSE, serviceResponse);
    }
}
