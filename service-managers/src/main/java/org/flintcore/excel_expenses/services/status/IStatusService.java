package org.flintcore.excel_expenses.services.status;

/**
 * Implementation must retrieves
 */
public interface IStatusService<R> {
    R getStatus();
    boolean isUp();
}
