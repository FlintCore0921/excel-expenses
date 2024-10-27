package org.flintcore.excel_expenses.managers.validators;

import org.flintcore.excel_expenses.managers.rules.ILocalBusinessRules;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LocalBusinessValidator {
    public boolean validateContent(LocalBusiness localBusiness) {
        return Objects.nonNull(localBusiness) &&
                validateRNC(localBusiness) &&
                validateName(localBusiness);
    }

    private boolean validateRNC(LocalBusiness localBusiness) {
        String rnc = localBusiness.RNC();
        String regex = "\\d{%d}".formatted(ILocalBusinessRules.RNC_SIZE);
        return Objects.nonNull(rnc) && rnc.matches(regex);
    }

    private boolean validateName(LocalBusiness localBusiness) {
        String name = localBusiness.name();
        return Objects.nonNull(name) && !name.isBlank();
    }
}
