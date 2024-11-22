package org.flintcore.excel_expenses.services.excels.external;

import org.flintcore.excel_expenses.models.expenses.IBusiness;

import java.util.List;
import java.util.concurrent.Future;

public interface ILocalBusinessService<B extends IBusiness> {
    Future<List<B>> getBusinessDataList();
}