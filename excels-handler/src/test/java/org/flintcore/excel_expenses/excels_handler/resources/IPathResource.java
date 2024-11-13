package org.flintcore.excel_expenses.excels_handler.resources;

import java.util.List;

public interface IPathResource {
    String getExternalExpensePath();

    default List<String> getExternalExpensePaths() {
        return List.of(getExternalExpensePath());
    }
}
