package com.flintcore.excel_expenses.handlers.routers.expenses;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EExpenseRoute implements IRoute {
    HOME("/templates/expensePage.fxml"),
    CREATE("");
    public final String route;
}
