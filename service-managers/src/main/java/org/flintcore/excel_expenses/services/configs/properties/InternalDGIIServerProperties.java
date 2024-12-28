package org.flintcore.excel_expenses.services.configs.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties(prefix = "external.services.dgii")
public record InternalDGIIServerProperties(
        int port,
        URI mainUri,
        Duration timeGapRequest,
        Path paths,
        Endpoints endpoints
) {
    public record Path(
            String cmdCommand,
            String filePath
    ) {}

    public record Endpoints(
            URI serverStatus,
            URI businessMain,
            URI businessGet,
            URI businessByStatus
    ) {}
}
