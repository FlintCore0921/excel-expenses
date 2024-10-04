package com.flintcore.excel_expenses.handlers.sorters;

import com.flintcore.excel_expenses.models.IOrderSortable;

import java.util.Comparator;

public interface IRouteSortFactory {
    Comparator<? super IOrderSortable> NUMERAL_ORDER_COMPARATOR = Comparator.comparing(IOrderSortable::getOrder);
}
