package org.flintcore.excel_expenses.excels_handler.tasks.business;

import lombok.AllArgsConstructor;
import org.flintcore.excel_expenses.excels_handler.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.excels_handler.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.excels_handler.models.lists.SerialListHolder;

import java.util.function.Supplier;

@AllArgsConstructor
public final class StoreSerializableLocalBusinessTask implements Supplier<Boolean> {
    private final LocalBusinessSerializeFileManager fileManager;
    private final SerialListHolder<LocalBusiness> infoSerialized;

    @Override
    public Boolean get() {
        try {
            fileManager.updateDataSet(infoSerialized);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
