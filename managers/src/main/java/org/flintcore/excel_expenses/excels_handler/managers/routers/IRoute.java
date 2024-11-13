package org.flintcore.excel_expenses.excels_handler.managers.routers;

import jakarta.annotation.Nullable;
import org.flintcore.excel_expenses.excels_handler.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.excels_handler.models.IOrderSortable;

public interface IRoute extends IOrderSortable {
    String name();
    String getRoute();
   @Nullable
   CompoundResourceBundle.Bundles[] getBundlePaths();
}
