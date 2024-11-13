package org.flintcore.excel_expenses.excels_handler.handlers.sorters;

import org.flintcore.excel_expenses.excels_handler.models.IOrderSortable;

import java.util.Comparator;

public interface IRouteSortFactory {
    Comparator<? super IOrderSortable> NUMERAL_ORDER_COMPARATOR = Comparator.comparing(IOrderSortable::getOrder);
}
