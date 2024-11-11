package org.flintcore.excel_expenses.services.management;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.config.BeanConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Also use the @Configuration or @TestConfiguration to component scope out of the module / project.
@SpringBootTest(classes = {
        ExcelFileManagementService.class,
        BeanConfiguration.class
})
class ExcelFileManagementServiceTest {
    @Autowired
    private ExcelFileManagementService excelFileManagementService;

    @Test
    void BuildFileByExcel() throws Exception {
        String path = "C:\\Users\\Elior\\Desktop\\FORMULARIO DE EXPENSE XPERTCODE- Julio - 2024 - Elio Erick Ramos Mosquea.xlsm";

        XSSFWorkbook workbook = excelFileManagementService.loadWorkBook(path).get();

        assertNotNull(workbook);

        // ??
        System.out.printf("Names: %s%n", workbook.getAllNames());

        int numberOfSheets = workbook.getNumberOfSheets();
        System.out.printf("No. Sheet: %s%n", numberOfSheets);

        for (int i = 0; i < numberOfSheets; i++) {
            System.out.printf("Sheet #%d name: %s%n", i+1, workbook.getSheetName(i));
        }
    }

    @Test
    void checkContains() {
        String path = "C:\\Users\\Elior\\Desktop";

        try (var paths = Files.newDirectoryStream(Path.of(path))) {

            for (Path entries : paths) {
                System.out.println(entries.getFileName());
            }
        } catch (IOException e) {
            Assertions.fail();
        }
    }
}