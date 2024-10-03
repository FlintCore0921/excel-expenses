package com.flintcore.excel_expenses.handlers.routers.main;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum EMainRoute implements IRoute {
    Home("/templates/Homepage.fxml"),
    EXPENSES("/templates/expensePage.fxml"),
    LOCALS(""),
    REGISTER("");

    public final String route;

    public static List<EMainRoute> availableRoutes() {
        return Arrays.stream(values())
                .filter(rt -> Objects.isNull(rt) || !rt.getRoute().isBlank())
                .toList();
    }
}
