package org.flintcore.excel_expenses.managers.routers.expenses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

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
