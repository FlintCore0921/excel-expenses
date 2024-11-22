package org.flintcore.excel_expenses.services.excels.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@ConfigurationProperties(prefix = "external.service.dgii")
public record DGIIApiProperties(@NonNull int port, @NonNull String url) {
}
