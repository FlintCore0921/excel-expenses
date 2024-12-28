package org.flintcore.excel_expenses.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@Log4j2
public class RemoteRequestHelper {

    private final RestTemplate requestCaller;

    public RemoteRequestHelper(
            @Qualifier("defaultBuilder") RestTemplate requestCaller
    ) {
        this.requestCaller = requestCaller;
    }

    public <T> ResponseEntity<T> getRequestFrom(
            URI urlPath,
            ParameterizedTypeReference<T> responseType
    ) {
        return this.requestCaller.exchange(urlPath, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getRequestFrom(
            URI urlPath,
            ParameterizedTypeReference<T> responseType,
            Map<String, ?> headers
    ) {
        return this.requestCaller.exchange(
                urlPath.toASCIIString(), HttpMethod.GET, null,
                responseType, headers
        );
    }
}
