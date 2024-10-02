package com.flintcore.excel_expenses.handlers.routers;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EMainRoute {
    Home("/templates/Homepage.fxml"),
    EXPENSES(""),
    LOCALS(""),
    REGISTER("");

    String resourceRoute;
}
