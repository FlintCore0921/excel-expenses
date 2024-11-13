package org.flintcore.excel_expenses.excels_handler.managers.routers.expenses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.excels_handler.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.excels_handler.managers.routers.IRoute;

@AllArgsConstructor
@Getter
public enum EExpenseItemRoute implements IRoute {
    CREATE("/templates/expenseItem.fxml", null);
    private final String route;
    private final Bundles[] bundlePaths;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
