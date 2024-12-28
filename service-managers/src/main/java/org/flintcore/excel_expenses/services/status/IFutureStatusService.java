package org.flintcore.excel_expenses.services.status;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface IFutureStatusService<R> extends IStatusService<Future<R>> {
}
