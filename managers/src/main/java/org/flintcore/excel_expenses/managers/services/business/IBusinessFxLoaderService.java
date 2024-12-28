package org.flintcore.excel_expenses.managers.services.business;

import javafx.collections.ObservableList;
import org.flintcore.excel_expenses.managers.services.ILoaderFxServiceStatus;
import org.flintcore.excel_expenses.models.business.IBusiness;

import java.util.concurrent.Future;

public interface IBusinessFxLoaderService<B extends IBusiness> extends ILoaderFxServiceStatus {
    Future<ObservableList<B>> getBusinessDataList();
}