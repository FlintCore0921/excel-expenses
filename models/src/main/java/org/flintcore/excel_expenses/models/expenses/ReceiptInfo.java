package org.flintcore.excel_expenses.models.expenses;

public record ReceiptInfo(
        Receipt receipt,
        LocalBusiness business
) {}
