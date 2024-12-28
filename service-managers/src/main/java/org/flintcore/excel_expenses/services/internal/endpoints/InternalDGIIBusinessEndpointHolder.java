package org.flintcore.excel_expenses.services.internal.endpoints;

import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.services.configs.properties.InternalDGIIServerProperties;

import java.net.URI;

@RequiredArgsConstructor
public class InternalDGIIBusinessEndpointHolder implements IBusinessEndpointHolder {
    private final InternalDGIIServerProperties properties;

    @Override
    public URI getBusinessEndpointURI() {
        return properties.mainUri().resolve(properties.endpoints().businessGet());
    }

    @Override
    public URI getBusinessByStatusEndpointURI() {
        return properties.mainUri().resolve(properties.endpoints().businessByStatus());
    }
}
