package org.flintcore.excel_expenses.handlers.routers;

import org.flintcore.excel_expenses.models.IOrderSortable;

public interface IRoute extends IOrderSortable {
    String getName();
    String getRoute();
}
