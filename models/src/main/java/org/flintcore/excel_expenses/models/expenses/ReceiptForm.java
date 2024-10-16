package org.flintcore.excel_expenses.models.expenses;

import lombok.Getter;

@Getter
public class ReceiptForm {

    private final Receipt.ReceiptBuilder receiptBuilder;
    private final LocalBusiness.LocalBusinessBuilder localBusinessBuilder;

    public ReceiptForm() {
        receiptBuilder = new Receipt.ReceiptBuilder();
        localBusinessBuilder = new LocalBusiness.LocalBusinessBuilder();
    }

    public ReceiptInfo buildInfo() {
        return new ReceiptInfo(
                receiptBuilder.build(),
                localBusinessBuilder.build()
        );
    }
}
