package org.flintcore.excel_expenses.models.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public final class DefaultPageListResponse<T> implements IPageListResponse<T> {
    private final int lastPage;
    @JsonAlias({"actual_page","curr_page"})
    private final int currentPage;
    @JsonAlias({"size","SIZE"})
    private final int pageSize;
    @JsonAlias("result")
    private final List<T> data;

    public DefaultPageListResponse(IPageListResponse<T> response) {
        this(response.getLastPage(),
                response.getCurrentPage(),
                response.getPageSize(),
                response.getData()
        );
    }
}
