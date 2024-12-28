package org.flintcore.excel_expenses.tasks.business;

import lombok.AllArgsConstructor;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;

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
