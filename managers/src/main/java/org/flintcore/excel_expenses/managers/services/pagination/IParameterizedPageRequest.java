package org.flintcore.excel_expenses.managers.services.pagination;

import java.util.Map;

public interface IParameterizedPageRequest extends IPageRequest {
    Map<String, ?> getParams();
}
