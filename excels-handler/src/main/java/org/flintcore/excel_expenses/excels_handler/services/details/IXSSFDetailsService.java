package org.flintcore.excel_expenses.excels_handler.services.details;

import java.util.concurrent.Future;

public interface IXSSFDetailsService<D, R> {
    Future<D> getDetailsOf(R r);
}
