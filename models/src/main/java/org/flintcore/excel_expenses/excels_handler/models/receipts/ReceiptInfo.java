package org.flintcore.excel_expenses.excels_handler.models.receipts;

import org.flintcore.excel_expenses.excels_handler.models.expenses.LocalBusiness;

public record ReceiptInfo(
        Receipt receipt,
        LocalBusiness business
) {}
