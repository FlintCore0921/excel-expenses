package org.flintcore.excel_expenses.managers.services.business;

import org.flintcore.excel_expenses.models.expenses.IBusiness;

import java.util.List;
import java.util.concurrent.Future;

public interface IBusinessStorageService<B extends IBusiness> {
    Future<Void> saveData(B business);
    Future<Void> saveData(List<B> business);
}