package org.flintcore.excel_expenses.models.requests;

import java.io.Serializable;

public interface IPageResponse<T> extends Serializable {
    int getLastPage();
    int getCurrentPage();
    int getPageSize();
    T getData();
}
