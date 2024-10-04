package com.flintcore.excel_expenses.handlers.routers;

import com.flintcore.excel_expenses.models.IOrderSortable;

public interface IRoute extends IOrderSortable {
    String getName();
    String getRoute();
}
