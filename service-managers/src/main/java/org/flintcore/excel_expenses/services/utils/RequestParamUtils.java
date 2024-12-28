package org.flintcore.excel_expenses.services.utils;

import org.springframework.core.ParameterizedTypeReference;

public final class RequestParamUtils {
    private RequestParamUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static <T> ParameterizedTypeReference<T> buildRequestParameter() {
        return new ParameterizedTypeReference<>() {
        };
    }
}
