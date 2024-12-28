package org.flintcore.excel_expenses.services.utils;

import org.flintcore.excel_expenses.managers.services.pagination.IPageRequest;
import org.flintcore.excel_expenses.managers.services.pagination.IParameterizedPageRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class PageRequestUtils {
    private PageRequestUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    /**
     * @see #hasValuesInRange(IPageRequest, int)
     */
    public static boolean hasValuesInRange(IPageRequest request, Supplier<Integer> size) {
        return hasValuesInRange(request, size.get());
    }

    /**
     * Checks is size of reference is in range of the Page Request.
     *
     * @return true if size can satisfy the endpoints.
     */
    public static boolean hasValuesInRange(IPageRequest request, int size) {
        int startIdx = (request.getPageIndex() - 1) * request.getPageSize(),
                endInd = startIdx + request.getPageSize();

        return size >= endInd;
    }

    public static Map<String, ?> asParams(IPageRequest request) {
        return Map.of(
                IPageRequest.SIZE_KEY, request.getPageSize(),
                IPageRequest.INDEX_KEY, request.getPageIndex()
        );
    }

    public static Map<String, ?> asParams(IParameterizedPageRequest request) {
        Map<String, Object> params = new HashMap<>(request.getParams());
        params.putAll(asParams((IPageRequest) request));
        return params;
    }
}
