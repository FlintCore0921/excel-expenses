package org.flintcore.excel_expenses.excels_handler.services.details;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.flintcore.excel_expenses.excels_handler.resources.PathResource;
import org.flintcore.excel_expenses.excels_handler.resources.XSSFUtils;
import org.flintcore.excelib.models.xssf.XSSFSheetDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        XSSFSheetDetailsService.class,
        PathResource.class,
})
@ActiveProfiles({"test"})
class XSSFSheetDetailsServiceTest {
    public static final String SHEET_NAME = "Sheet called ow";
    @Autowired
    private XSSFSheetDetailsService sheetDetailsService;
    @Autowired
    private PathResource pathResource;

    @Value("${spring.profiles.active}")
    String profile;

    @Test
    void readInternalSheet() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(wb -> {
                XSSFSheet sheet = wb.createSheet(SHEET_NAME);

                {
                    XSSFSheet temporal = wb.getSheetAt(0);
                    assertNotNull(temporal);
                    assertEquals(temporal, sheet);
                }

                XSSFSheetDetails sheetDetails = this.sheetDetailsService.getDetailsOf(sheet)
                        .get();

                System.out.println(sheetDetails);

                assertNotNull(sheetDetails);

                assertEquals(sheetDetails.name(), sheet.getSheetName());
            });
        });
    }

    @Test
    void readExternalSheet() {
        assertDoesNotThrow(() -> {
            // HAS 3 pages / sheets
            final int expected = 3;
            File expenseFile = Path.of(pathResource.getExternalExpensePath()).toFile();
            XSSFUtils.handleEmptyWorkbook(new FileInputStream(expenseFile), wb -> {
                int numberOfSheets = wb.getNumberOfSheets();

                assertEquals(expected, numberOfSheets,
                        "This sheet must has %d sheet inside it.".formatted(expected));
            });
        });
    }
}