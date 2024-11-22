package org.flintcore.excel_expenses.expenses;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.flintcore.excel_expenses.models.receipts.Receipt;

import java.nio.file.Path;
import java.util.List;

public record ExpenseBuilderHolder(
        @Nullable Path filePath,
        @NonNull String sheetName, @NonNull String tableName,
        @NonNull List<Receipt> receipts
) {
}
