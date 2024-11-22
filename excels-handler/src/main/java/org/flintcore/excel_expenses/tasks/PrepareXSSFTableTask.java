package org.flintcore.excel_expenses.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;

import java.util.function.BiFunction;

/**
 * Create a sheet with the given name, but only if not exists.
 */
@RequiredArgsConstructor
@Log4j2
public class PrepareXSSFTableTask implements BiFunction<XSSFSheet, String, XSSFTable> {
    private final AreaReference tableArea;

    @Override
    public XSSFTable apply(XSSFSheet sheet, String tableName) {
        return createTable(sheet, tableName);
    }

    private XSSFTable createTable(XSSFSheet sheet, String tableName) {
        return ObjectUtils.getIfNull(
                sheet.getWorkbook().getTable(tableName),
                () -> sheet.createTable(this.tableArea)
        );
    }
}
