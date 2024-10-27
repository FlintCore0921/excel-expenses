package org.flintcore.excel_expenses.models.receipts;

import org.flintcore.excel_expenses.models.expenses.LocalBusiness;

public record ReceiptInfo(
        Receipt receipt,
        LocalBusiness business
) {}
