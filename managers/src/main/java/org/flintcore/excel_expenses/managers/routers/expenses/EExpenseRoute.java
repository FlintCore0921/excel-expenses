package org.flintcore.excel_expenses.managers.routers.expenses;

import org.flintcore.excel_expenses.managers.routers.IRoute;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EExpenseRoute implements IRoute {
    CREATE("/templates/createExpenseForm.fxml");
    private final String route;

    @Override
    public String getName() {
        return name();
    }


    @Override
    public int getOrder() {
        return ordinal();
    }
}
