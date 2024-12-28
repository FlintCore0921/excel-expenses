package org.flintcore.excel_expenses.services.mappers;

import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class ConvertListPageResponse<T, R>
        implements Function<IPageListResponse<T>, IPageListResponse<R>> {

    protected final Function<T, R> mapper;

    /**
     * Creates a mapped list from given one.
     * @return No mutable collection.
     */
    protected List<R> mapData(Collection<T> data) {
        return data.stream().map(mapper).collect(Collectors.toList());
    }
}
