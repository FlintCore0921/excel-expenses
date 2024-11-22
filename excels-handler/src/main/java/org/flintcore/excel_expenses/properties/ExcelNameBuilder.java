package org.flintcore.excel_expenses.properties;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public abstract class ExcelNameBuilder {
    protected final String format;
    @Setter
    protected String name;

    public abstract String buildName();
}
