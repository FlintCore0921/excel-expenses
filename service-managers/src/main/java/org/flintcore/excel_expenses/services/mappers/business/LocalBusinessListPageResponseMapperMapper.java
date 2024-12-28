package org.flintcore.excel_expenses.services.mappers.business;

import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.requests.DefaultPageListResponse;
import org.flintcore.excel_expenses.models.requests.IPageListResponse;
import org.flintcore.excel_expenses.services.dto.LocalBusinessDto;
import org.flintcore.excel_expenses.services.utils.dtos.BusinessDtoMapper;

import java.util.function.Function;

public class LocalBusinessListPageResponseMapperMapper
        extends BusinessListPageResponseMapper<LocalBusinessDto, LocalBusiness> {

    public LocalBusinessListPageResponseMapperMapper() {
        this(BusinessDtoMapper::getAsLocalBusiness);
    }

    public LocalBusinessListPageResponseMapperMapper(
            Function<LocalBusinessDto, LocalBusiness> mapper
    ) {
        super(mapper);
    }


    @Override
    public IPageListResponse<LocalBusiness> apply(IPageListResponse<LocalBusinessDto> dtoResponse) {
        return new DefaultPageListResponse<LocalBusiness>(
                dtoResponse.getLastPage(), dtoResponse.getCurrentPage(), dtoResponse.getPageSize(),
                this.mapData(dtoResponse.getData())
        );
    }
}
