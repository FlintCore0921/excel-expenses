package org.flintcore.excel_expenses.utils;

import java.nio.file.Path;

// Class to create Excel file property.
public abstract class ExcelPathBuilder {
    public abstract Path buildPath(Path directory, final String name);
}
