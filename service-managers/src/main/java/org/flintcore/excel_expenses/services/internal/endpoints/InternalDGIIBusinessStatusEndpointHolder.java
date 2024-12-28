package org.flintcore.excel_expenses.services.internal.endpoints;

import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;

import java.net.URI;

@RequiredArgsConstructor
public class InternalDGIIBusinessStatusEndpointHolder implements IStatusEndpointHolder {
    private final InternalDGIIServerProperties properties;

    @Override
    public URI getStatusEndpointURI() {
        return properties.mainUri().resolve(properties.endpoints().serverStatus());
    }
}
