package org.flintcore.excel_expenses.managers.routers;

public interface IRouter<I extends IRoute> {
    String TEMPLATE_LOCATION = "/templates";

    void navigateBack();
}
