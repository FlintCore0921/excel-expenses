package org.flintcore.excel_expenses.excels_handler.models.receipts;

import lombok.Getter;
import org.flintcore.excel_expenses.excels_handler.models.expenses.LocalBusiness;

@Getter
public class ReceiptForm {

    private final Receipt.ReceiptBuilder receiptBuilder;
    private final LocalBusiness.LocalBusinessBuilder localBusinessBuilder;

    public ReceiptForm() {
        receiptBuilder = new Receipt.ReceiptBuilder();
        localBusinessBuilder = LocalBusiness.builder();
    }

    public ReceiptInfo buildInfo() {
        return new ReceiptInfo(
                receiptBuilder.build(),
                localBusinessBuilder.build()
        );
    }
}
