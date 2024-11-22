package org.flintcore.excel_expenses.services.excels.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

import java.time.Duration;

@ConfigurationProperties(prefix = "external.services.dgii")
public record DGIIAPIProperties(@NonNull int port, @NonNull String url, Duration timeGapRequest) {
}
