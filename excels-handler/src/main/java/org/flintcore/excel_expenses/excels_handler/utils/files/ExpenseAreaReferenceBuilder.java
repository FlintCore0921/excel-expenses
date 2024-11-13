package org.flintcore.excel_expenses.excels_handler.utils.files;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@Scope("prototype")
@Lazy
public class ExpenseAreaReferenceBuilder {
    private static final int TABLE_COLUMNS = 8;

    public AreaReference buildAreaReference(
            Sheet sheet, int rows,
            SpreadsheetVersion spreadsheetVersion
    ) {
        int startRowIndex = sheet.getLastRowNum() + 2;
        int endRowIndex = startRowIndex + rows;

        for (int i = startRowIndex; i < endRowIndex; i++) {
            sheet.createRow(i);
        }

        var startCellReference = new CellReference(startRowIndex, TABLE_COLUMNS);
        var endCellReference = new CellReference(endRowIndex, TABLE_COLUMNS);

        return new AreaReference(startCellReference, endCellReference, spreadsheetVersion);
    }
}
