package org.flintcore.excel_expenses.managers.routers;

import jakarta.annotation.Nullable;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.models.IOrderSortable;

public interface IRoute extends IOrderSortable {
    String name();
    String getRoute();
   @Nullable
   CompoundResourceBundle.Bundles[] getBundlePaths();
}
