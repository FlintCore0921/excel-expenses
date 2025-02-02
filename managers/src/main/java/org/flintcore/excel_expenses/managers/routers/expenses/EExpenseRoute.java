package org.flintcore.excel_expenses.managers.routers.expenses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

import static org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles.EXPENSE_CREATE_FORM;
import static org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles.LOCAL_RECEIPT;

@AllArgsConstructor
@Getter
public enum EExpenseRoute implements IRoute {
    CREATE("/templates/createExpenseForm.fxml",
            new Bundles[]{EXPENSE_CREATE_FORM, LOCAL_RECEIPT}
    );
    private final String route;
    private final Bundles[] bundlePaths;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
