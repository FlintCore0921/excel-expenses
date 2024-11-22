package org.flintcore.excel_expenses.excels_handler.expenses;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;

import java.nio.file.Path;
import java.util.List;

public record ExpenseBuilderHolder(
        @Nullable Path filePath,
        @NonNull String sheetName, @NonNull String tableName,
        @NonNull List<Receipt> receipts
) {
}
