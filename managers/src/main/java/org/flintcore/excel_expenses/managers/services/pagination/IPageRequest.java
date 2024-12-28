package org.flintcore.excel_expenses.managers.services.pagination;

public interface IPageRequest {
    String INDEX_KEY = "page";
    String SIZE_KEY = "size";

    int getPageIndex();

    int getPageSize();
}
