package org.flintcore.excel_expenses.excels_handler.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Objects;
import java.util.function.Function;

/**
 * Create a sheet with the given name, but only if not exists.
 */
@RequiredArgsConstructor
@Log4j2
public class PrepareXSSFSheetTask implements Function<String, XSSFSheet> {
    private final XSSFWorkbook workbook;

    @Override
    public XSSFSheet apply(String sheetName) {
        return createSheet(sheetName);
    }

    private XSSFSheet createSheet(String sheetName) {
        XSSFSheet sheet = null;

        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            sheet = workbook.getSheetAt(index);
            if (sheet.getSheetName().equals(sheetName)) break;
        }

        return Objects.nonNull(sheet) ? sheet : workbook.createSheet(sheetName);
    }
}
