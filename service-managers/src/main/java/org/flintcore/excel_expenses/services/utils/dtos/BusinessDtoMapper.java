package org.flintcore.excel_expenses.services.utils.dtos;

import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.services.dto.LocalBusinessDto;

public final class BusinessDtoMapper {
    private BusinessDtoMapper() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static LocalBusiness getAsLocalBusiness(LocalBusinessDto businessDto) {
        return new LocalBusiness(businessDto.rnc(), businessDto.name());
    }
}
