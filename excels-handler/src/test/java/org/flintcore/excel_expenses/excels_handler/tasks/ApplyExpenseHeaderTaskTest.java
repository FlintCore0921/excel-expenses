package org.flintcore.excel_expenses.excels_handler.tasks;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.flintcore.excel_expenses.excels_handler.resources.XSSFUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringJUnitConfig
class ApplyExpenseHeaderTaskTest {

    public static final String SHEET_NAME = "sheet1";
    public static final String TEST_EXPENSES_XLSX = "test_expenses.xlsx";

    @Test
    void shouldCreateHeader() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(wb -> {
                ApplyExpenseHeaderTask applyExpenseHeaderTask = new ApplyExpenseHeaderTask();

                XSSFSheet sheet = wb.createSheet(SHEET_NAME);

                AreaReference tableArea = new AreaReference(
                        new CellReference(0, 0),
                        new CellReference(5, 5),
                        wb.getSpreadsheetVersion()
                );

                XSSFTable table = sheet.createTable(tableArea);

                applyExpenseHeaderTask.apply(table);

                try (FileOutputStream outputStream = new FileOutputStream(TEST_EXPENSES_XLSX)) {
                    wb.write(outputStream);
                }
            });
        });
    }
}