package org.flintcore.excel_expenses.tasks;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.flintcore.excel_expenses.expenses.EExpenseCellData;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import java.util.function.UnaryOperator;

public class XSSFApplyExpenseHeaderTask implements UnaryOperator<XSSFTable> {
    public static final String TABLE_HEADER_NAME = "Money";
    public static final String[] EXPENSE_COLUMN_HEADERS = EExpenseCellData.COLUMN_HEADERS;
    public static final int HEADER_SIZE = EXPENSE_COLUMN_HEADERS.length;

    @Override
    public XSSFTable apply(XSSFTable table) {
        validateArea(table);

        // Set the table name if blank.
        if (StringUtil.isBlank(table.getName())) {
            table.setName(TABLE_HEADER_NAME);
        }

        createHeaderRow(table);

        table.updateReferences();

        return table;
    }

    private void validateArea(XSSFTable table) {
        if (table.getColumns().size() == HEADER_SIZE) return;

        AreaReference area = table.getArea();
        CellReference lastCell = area.getLastCell();
        var endCell = new CellReference(lastCell.getRow(), HEADER_SIZE - 1);

        SpreadsheetVersion spreadsheetVersion = table.getXSSFSheet().getWorkbook()
                .getSpreadsheetVersion();

        var newArea = new AreaReference(area.getFirstCell(), endCell, spreadsheetVersion);
        table.setArea(newArea);
        table.getCTTable().setRef(newArea.formatAsString());
    }

    private void createHeaderRow(XSSFTable table) {
        XSSFSheet sheet = table.getXSSFSheet();
        final int headerRowIndex = table.getStartRowIndex();

        XSSFRow row = ObjectUtils.getIfNull(
                sheet.getRow(headerRowIndex),
                () -> sheet.createRow(headerRowIndex)
        );

        CTTable ctTable = table.getCTTable();

        // 1. Set up the table structure
        // Do not create the table column
        // Checks if have later
//        ctTable.addNewTableColumns();
        CTTableColumns columns = ObjectUtils.getIfNull(
                ctTable.getTableColumns(),
                ctTable::addNewTableColumns
        );

//         Add new columns
        for (int i = 0; i < HEADER_SIZE; i++) {
            if(columns.getCount() <= i)
                columns.addNewTableColumn();

            CTTableColumn ctTableColumn = columns.getTableColumnArray(i);

            applyCellText(row, i, EXPENSE_COLUMN_HEADERS[i]);
            ctTableColumn.setId(i + 1);
        }

        applyFilter(table, ctTable);

        // 2. Set the correct count before manipulating columns
        columns.setCount(HEADER_SIZE);
    }

    private void applyCellText(XSSFRow row, int i, String expenseColumnHeader) {
        var cell = ObjectUtils.getIfNull(
                row.getCell(i),
                () -> row.createCell(i)
        );

        cell.setCellValue(expenseColumnHeader);
    }

    private void applyFilter(XSSFTable table, CTTable ctTable) {
        CTAutoFilter ctAutoFilter = ctTable.addNewAutoFilter();
        ctAutoFilter.setRef(table.getArea().formatAsString());
    }
}
