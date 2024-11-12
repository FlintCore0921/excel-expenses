package org.flintcore.excel_expenses.services.details;

import java.util.concurrent.Future;

public interface IXSSFDetailsService<D, R> {
    Future<D> getDetailsOf(R r);
}
