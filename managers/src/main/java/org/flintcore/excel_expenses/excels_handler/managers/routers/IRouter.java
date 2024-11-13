package org.flintcore.excel_expenses.excels_handler.managers.routers;

public interface IRouter<I extends IRoute> {
    String TEMPLATE_LOCATION = "/templates";

    void navigateBack();
}
