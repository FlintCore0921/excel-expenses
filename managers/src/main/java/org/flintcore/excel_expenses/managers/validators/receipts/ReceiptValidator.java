package org.flintcore.excel_expenses.managers.validators.receipts;

import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.managers.validators.NFCValidator;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ReceiptValidator {
    protected static final double INVALID_PRICE = 0.0;

    private final NFCValidator NFCvalidator;

    public ReceiptValidator(NFCValidator NFCvalidator) {
        this.NFCvalidator = NFCvalidator;
    }

    public boolean validate(Receipt receipt) {
        return ObjectUtils.allNotNull(
                receipt.business(),
                receipt.dateCreation(),
                receipt.NFC()
        ) && NFCvalidator.test(receipt.NFC()) && isValidPrice(receipt.price());
    }

    private boolean isValidPrice(double price) {
        return Double.compare(price, INVALID_PRICE) > 0;
    }


}
