package org.flintcore.excel_expenses.services.configs;

import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.services.RemoteRequestHelper;
import org.flintcore.excel_expenses.services.connections.IServerConnection;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessEndpointHolder;
import org.flintcore.excel_expenses.services.internal.endpoints.InternalDGIIBusinessStatusEndpointHolder;
import org.flintcore.excel_expenses.services.internal.status.InternalBusinessStatusFxService;
import org.flintcore.excel_expenses.services.requests.business.InternalBusinessService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Supplier;

/**
 * Configs form business server in local and pre initialize.
 */
@Configuration
@Profile({"dev", "test", "internal-py-server"})
public class InternalDGIIServerConfiguration {

    @Bean
    public InternalBusinessStatusFxService internalBusinessStatusService(
             GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutdownHandler,
            RemoteRequestHelper requestHelper,
            InternalDGIIBusinessStatusEndpointHolder internalStatusEndpointHolder
    ) {
        return new InternalBusinessStatusFxService(
                eventHandler, shutdownHandler, requestHelper, internalStatusEndpointHolder
        );
    }

    @Bean
    public InternalBusinessService internalBusinessPageService(
            Supplier<IServerConnection> connectionSupplier,
            InternalBusinessStatusFxService statusService,
            RemoteRequestHelper requestHelper,
            InternalDGIIBusinessEndpointHolder internalStatusEndpointHolder
    ) {
        return new InternalBusinessService(
                connectionSupplier,
                statusService, requestHelper,
                internalStatusEndpointHolder
        );
    }

}
