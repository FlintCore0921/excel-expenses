package org.flintcore.excel_expenses.excels_handler.managers.factories.receipts;

import data.utils.NullableUtils;
import jakarta.annotation.Nullable;
import org.flintcore.excel_expenses.excels_handler.managers.rules.IReceiptRules;
import org.flintcore.excel_expenses.excels_handler.managers.validators.NFCValidator;
import org.flintcore.excel_expenses.excels_handler.models.receipts.LocalNFCCode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@Scope("prototype")
public class LocalNFCFactory implements UnaryOperator<String> {
    private NFCValidator nfcValidator;

    @Override
    @Nullable
    public String apply(final String NFC) {
        initValidator();

        if (this.nfcValidator.test(NFC)) return NFC;

        if (!this.nfcValidator.canStartApply(NFC)) return null;

        StringBuilder sb = new StringBuilder();
        LocalNFCCode startCode = extractNFCCode(NFC);

        String rightPart = NFC.substring(1);
        int leadingSize = calculateLeadingSize() - rightPart.length();

        sb.append(startCode.fullPrefix);
        sb.append("0".repeat(leadingSize));
        sb.append(rightPart);

        return sb.toString();
    }

    private int calculateLeadingSize() {
        return IReceiptRules.NFC_CODE_LENGTH - LocalNFCCode.FULL_SIZE;
    }

    private static LocalNFCCode extractNFCCode(String NFC) {
        String upperCode = NFC.substring(0, 1).toUpperCase();
        return LocalNFCCode.valueOf(upperCode);
    }

    private void initValidator() {
        NullableUtils.executeIsNull(this.nfcValidator,
                () -> this.nfcValidator = new NFCValidator()
        );
    }
}
