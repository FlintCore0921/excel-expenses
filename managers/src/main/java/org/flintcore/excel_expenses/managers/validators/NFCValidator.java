package org.flintcore.excel_expenses.managers.validators;

import org.flintcore.excel_expenses.managers.rules.IReceiptRules;
import org.flintcore.excel_expenses.models.receipts.LocalNFCCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;

@Component
@Scope("prototype")
public class NFCValidator implements Predicate<String> {

    @Override
    public boolean test(final String NFC) {
        return isContentLengthValid(NFC) && canStartApply(NFC) && isContentMatches(NFC);
    }

    /**
     * Checks if at least the value may be converted or can be mapped.
     * Checks if starts with a valid {@link LocalNFCCode NFC code}.
     */
    public boolean canStartApply(final String NFC) {
        return Arrays.stream(LocalNFCCode.values())
                .map(Enum::name)
                .anyMatch(NFC.toUpperCase()::startsWith);
    }

    private boolean isContentMatches(final String NFC) {
        String pattern = IReceiptRules.getCodePattern();
        return NFC.matches(pattern);
    }

    private boolean isContentLengthValid(String NFC) {
        return !NFC.trim().isBlank() && NFC.length() == IReceiptRules.NFC_CODE_LENGTH;
    }
}
