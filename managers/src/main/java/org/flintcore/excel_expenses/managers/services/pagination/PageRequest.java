package org.flintcore.excel_expenses.managers.services.pagination;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class PageRequest implements IPageRequest {
    private final int pageIndex;
    private final int pageSize;

    public static PageRequest of(int pageIndex, int pageSize) {
        return new PageRequestBuilder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .build();
    }

    public static PageRequest defaultPage() {
        return of(1, 10);
    }
}
