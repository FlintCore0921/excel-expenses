package org.flintcore.excel_expenses.excels_handler.resources;

import java.util.List;

public interface IPathResource {
    String getExternalExpensePath();

    String getSecondaryExpensePath();

    default List<String> getExternalExpensePaths() {
        return List.of(
                getExternalExpensePath(),
                getSecondaryExpensePath()
        );
    }
}
