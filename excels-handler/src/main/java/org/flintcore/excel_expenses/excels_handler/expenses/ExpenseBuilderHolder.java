package org.flintcore.excel_expenses.excels_handler.expenses;

import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;

import java.nio.file.Path;
import java.util.List;

public record ExpenseBuilderHolder(
        Path filePath,
        String sheetName, String tableName,
        List<Receipt> receipts
) {}
