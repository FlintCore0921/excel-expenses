package org.flintcore.excel_expenses.managers.services.pagination;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ParameterizedPageRequest implements IPageRequest {
    private final int pageIndex;
    private final int pageSize;

    private final Map<String, Object> params;
}
