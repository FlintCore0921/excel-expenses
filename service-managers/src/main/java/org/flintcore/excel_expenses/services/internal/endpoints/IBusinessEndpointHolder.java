package org.flintcore.excel_expenses.services.internal.endpoints;

import java.net.URI;

public interface IBusinessEndpointHolder extends IRequestEndpointHolder{
    URI getBusinessEndpointURI();
    URI getBusinessByStatusEndpointURI();
}
