package org.flintcore.excel_expenses.tasks.receipts;

import lombok.AllArgsConstructor;
import org.flintcore.excel_expenses.files.receipts.PeriodReceiptSerializeFileManager;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;

import java.time.LocalDate;
import java.util.function.Supplier;

@AllArgsConstructor
public final class StoreSerializableReceiptTask implements Supplier<Boolean> {
    private final PeriodReceiptSerializeFileManager fileManager;
    private final SerialListHolder<Receipt> info;

    @Override
    public Boolean get() {
        try {
            LocalDate localDate = LocalDate.now();
            fileManager.setOnMonth(localDate.getMonth());
            fileManager.setOnYear(localDate.getYear());
            fileManager.updateDataSet(info);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
