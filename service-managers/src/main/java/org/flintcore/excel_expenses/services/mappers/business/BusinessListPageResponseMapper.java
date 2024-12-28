package org.flintcore.excel_expenses.services.mappers.business;

import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.excel_expenses.services.mappers.ConvertListPageResponse;

import java.util.function.Function;

public abstract class BusinessListPageResponseMapper<T, R extends IBusiness>
        extends ConvertListPageResponse<T, R> {
    public BusinessListPageResponseMapper(Function<T, R> mapper) {
        super(mapper);
    }
}
