package org.flintcore.excel_expenses.services.excels.external;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AliasFor;

@ConfigurationProperties(prefix = "external.services.paths")
public record DGIIReferenceProperties(String py_head, @AliasFor("local-dgii") String localPath) {

}
