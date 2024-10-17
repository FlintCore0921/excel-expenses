package org.flintcore.excel_expenses.managers.routers.expenses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.routers.IRoute;

@AllArgsConstructor
@Getter
public enum EExpenseRoute implements IRoute {
    CREATE("/templates/createExpenseForm.fxml");
    private final String route;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
