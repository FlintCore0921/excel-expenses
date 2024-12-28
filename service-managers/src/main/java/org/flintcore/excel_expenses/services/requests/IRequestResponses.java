package org.flintcore.excel_expenses.services.requests;

import org.flintcore.excel_expenses.models.requests.DefaultPageListResponse;

import java.util.Collections;

public interface IRequestResponses {
    DefaultPageListResponse<?> EMPTY_LIST_PAGE_RESPONSE = new DefaultPageListResponse<>(
            1, 1, 1, Collections.emptyList());

}
