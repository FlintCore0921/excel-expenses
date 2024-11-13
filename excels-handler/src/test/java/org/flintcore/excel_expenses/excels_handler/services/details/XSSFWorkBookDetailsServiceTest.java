package org.flintcore.excel_expenses.excels_handler.services.details;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excelib.models.xssf.XSSFSheetDetails;
import org.flintcore.excelib.models.xssf.XSSFTableDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        XSSFWorkBookDetailsService.class,

})
class XSSFWorkBookDetailsServiceTest {
    @Autowired
    private XSSFWorkBookDetailsService workBookDetailsService;

    @Test
    void createDetailsOfEmptyFile() {
        assertDoesNotThrow(() -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                var workbookDetails = workBookDetailsService.getDetailsOf(workbook)
                        .get();

                assertNotNull(workbookDetails);
                assertTrue(workbookDetails.sheetInfos().isEmpty(), "Workbook sheets must be empty.");
                assertTrue(workbookDetails.getAllTablesDetails().isEmpty(), "Workbook tables must be empty.");
            }

        });
    }

    @DisplayName("Workbook with sheets.")
    @ParameterizedTest(name = "Workbook with {0} sheets.")
    @ValueSource(ints = {1, 3, 6, 19, 30})
    void createDetailsOfSomeSheets(int sheets) {
        assertDoesNotThrow(() -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                for (int ind = 0; ind < sheets; ind++) {
                    workbook.createSheet("Sheet #%d".formatted(ind));
                }

                var workbookDetails = workBookDetailsService.getDetailsOf(workbook).get();

                assertNotNull(workbookDetails);

                List<XSSFSheetDetails> sheetDetailsList = workbookDetails.sheetInfos();
                assertFalse(sheetDetailsList.isEmpty(),
                        "Workbook sheets must not be empty.");

                assertEquals(sheets, sheetDetailsList.size(),
                        "Workbook sheets must have the same number of sheets.");
            }
        });
    }

    @DisplayName("Workbook with tables.")
    @ParameterizedTest(name = "Workbook with {0} sheets, each with {1} tables.")
    @MethodSource("org.flintcore.excel_expenses.excels_handler.resources.XSSFDataSource#sheetWithTablesArgs")
    void createDetailsOfSomeSheetsAndTables(int sheets, int tables) {
        assertDoesNotThrow(() -> {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {

                for (int ind = 0; ind < sheets; ind++) {
                    XSSFSheet sheet = workbook.createSheet("Sheet #%d".formatted(ind));

                    int row = 0, col = 0;

                    for (int tbInd = 0; tbInd < tables; tbInd++) {
                        CellReference startReference = new CellReference(row, col);
                        CellReference endReference = new CellReference(row + 1, col + 1);

                        AreaReference areaReference = workbook.getCreationHelper()
                                .createAreaReference(startReference, endReference);

                        sheet.createTable(areaReference);

                        col += 3;
                    }
                }

                var workbookDetails = workBookDetailsService.getDetailsOf(workbook).get();

                assertNotNull(workbookDetails);

                List<XSSFSheetDetails> sheetDetailsList = workbookDetails.sheetInfos();
                assertFalse(sheetDetailsList.isEmpty(),
                        "Workbook sheets must not be empty.");

                assertEquals(sheets, sheetDetailsList.size(),
                        "Workbook sheets must have the same number of sheets.");

                List<XSSFTableDetails> tablesDetails = workbookDetails.getAllTablesDetails();

               var containsAllTables = tablesDetails.stream().allMatch(table ->
                        sheetDetailsList.stream()
                                .anyMatch(sheet -> sheet.tables().contains(table))
                );

               assertTrue(containsAllTables);
            }
        });
    }

}