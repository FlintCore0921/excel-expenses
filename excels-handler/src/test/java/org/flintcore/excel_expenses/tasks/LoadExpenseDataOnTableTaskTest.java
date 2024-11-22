package org.flintcore.excel_expenses.tasks;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.resources.ExpenseDataSource;
import org.flintcore.excel_expenses.resources.XSSFUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LoadExpenseDataOnTableTaskTest {


    public static final String TEST_EXPENSES_XLSX = "test_expenses.xlsx";

    @Test
    void shouldCreateDataOnWorkBook() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(wb -> {
                XSSFSheet sheet = wb.createSheet("Sheet accord");

                var tableRef = new AreaReference("A1:D6", wb.getSpreadsheetVersion());

                XSSFTable table = sheet.createTable(tableRef);

                LoadExpenseDataOnTableTask<Receipt> onTableTask =
                        new LoadExpenseDataOnTableTask<>();

                List<Receipt> list = ExpenseDataSource.receiptListDataSource();
                onTableTask.apply(table, list);

                try (FileOutputStream outputStream = new FileOutputStream(TEST_EXPENSES_XLSX)) {
                    wb.write(outputStream);
                }
            });
        });
    }

    @Test
    void shouldCreateDataWithHeadersOnWorkBook() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(wb -> {
                XSSFSheet sheet = wb.createSheet("Sheet_accord");

                var areaTableRef = new AreaReference("A1:D5", wb.getSpreadsheetVersion());

                XSSFTable table = sheet.createTable(areaTableRef);

                {
//                     Set the headers.
                    XSSFApplyExpenseHeaderTask headerTask = new XSSFApplyExpenseHeaderTask();
                    headerTask.apply(table);
                }

                LoadExpenseDataOnTableTask<Receipt> onTableTask =
                        new LoadExpenseDataOnTableTask<>();

                List<Receipt> list = ExpenseDataSource.receiptListDataSource();
                onTableTask.apply(table, list);

                {
                    // Set style
                    new ApplyMediumStyleTask().apply(table);
                }

                try (FileOutputStream outputStream = new FileOutputStream(TEST_EXPENSES_XLSX)) {
                    wb.write(outputStream);
                }
            });
        });
    }

    @Test
    void shouldCreateDataWithHeadersOnWorkBook2() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(wb -> {
                XSSFSheet sheet = wb.createSheet("Sheet_accord");

                AreaReference reference = wb.getCreationHelper().createAreaReference(
                        new CellReference(0, 0), new CellReference(2, 2));

                // Create
                //creates a table having 3 columns as of area reference
                XSSFTable table = sheet.createTable(reference);
                // but all of those have id 1, so we need repairing
                table.getCTTable().getTableColumns().getTableColumnArray(1).setId(2);
                table.getCTTable().getTableColumns().getTableColumnArray(2).setId(3);

                table.setName("Test");
                table.setDisplayName("Test_Table");

                // For now, create the initial style in a low-level way
                table.getCTTable().addNewTableStyleInfo();
                table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

                // Style the table
                XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
                style.setName("TableStyleMedium2");
                style.setShowColumnStripes(false);
                style.setShowRowStripes(true);
                style.setFirstColumn(false);
                style.setLastColumn(false);
                style.setShowRowStripes(true);
                style.setShowColumnStripes(true);

                // Set the values for the table
                XSSFRow row;
                XSSFCell cell;
                for (int i = 0; i < 3; i++) {
                    // Create row
                    row = sheet.createRow(i);
                    for (int j = 0; j < 3; j++) {
                        // Create cell
                        cell = row.createCell(j);
                        if (i == 0) {
                            cell.setCellValue("Column" + (j + 1));
                        } else {
                            cell.setCellValue((i + 1.0) * (j + 1.0));
                        }
                    }
                }

                try (FileOutputStream outputStream = new FileOutputStream(TEST_EXPENSES_XLSX)) {
                    wb.write(outputStream);
                }
            });
        });
    }

    @Test
    void openFile() {
        assertDoesNotThrow(() -> {
            FileInputStream stream = new FileInputStream("test_expenses1.xlsx");
            XSSFUtils.handleEmptyWorkbook(stream, wb -> {
                XSSFSheet sheet = wb.getSheetAt(0);

                XSSFTable xssfTable = sheet.getTables().get(0);

                var ctTable = xssfTable.getCTTable();
            });
        });
    }
}