package org.flintcore.excel_expenses.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Objects;
import java.util.function.Function;

/**
 * Create a sheet with the given name, but only if not exists.
 */
@RequiredArgsConstructor
@Log4j2
public class PrepareHSSFSheetTask implements Function<String, HSSFSheet> {
    private final HSSFWorkbook workbook;

    @Override
    public HSSFSheet apply(String sheetName) {
        return createSheet(sheetName);
    }

    private HSSFSheet createSheet(String sheetName) {
        HSSFSheet sheet = null;

        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            sheet = workbook.getSheetAt(index);
            if (sheet.getSheetName().equals(sheetName)) break;
        }

        return Objects.nonNull(sheet) ? sheet : workbook.createSheet(sheetName);
    }
}
