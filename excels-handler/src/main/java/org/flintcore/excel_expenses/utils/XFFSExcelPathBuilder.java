package org.flintcore.excel_expenses.utils;

import org.flintcore.excel_expenses.models.files.EFileExtension;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class XFFSExcelPathBuilder extends ExcelPathBuilder {
    private static final EFileExtension EXTENSION = EFileExtension.XLSX;

    @Override
    public Path buildPath(Path directory, String name) {
        String nameWithExtension = name + EXTENSION.asDotExtension();
        return directory.resolve(nameWithExtension);
    }
}
