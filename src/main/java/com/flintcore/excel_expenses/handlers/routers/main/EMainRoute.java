package com.flintcore.excel_expenses.handlers.routers.main;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public enum EMainRoute {
    Home("/templates/Homepage.fxml"),
    EXPENSES("/templates/expensePage.fxml"),
    LOCALS(""),
    REGISTER("");

    public final String resourceRoute;

    public static List<EMainRoute> availableRoutes() {
        return Arrays.stream(values())
                .filter(rt -> Objects.isNull(rt) || !rt.resourceRoute.isBlank())
                .toList();
    }
}
