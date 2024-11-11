package org.flintcore.excel_expenses.services.management;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.config.BeanConfiguration;
import org.flintcore.excel_expenses.utils.XFFSExcelPathBuilder;
import org.flintcore.excel_expenses.utils.files.FileCreator;
import org.flintcore.excel_expenses.utils.paths.PathValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        ExcelFileCreatorService.class,
        XFFSExcelPathBuilder.class,
        FileCreator.class,
        PathValidator.class,
        BeanConfiguration.class
})
class ExcelFileCreatorServiceTest {
    @Autowired
    private ExcelFileCreatorService excelFileCreatorService;
    @Autowired
    private XFFSExcelPathBuilder pathBuilder;

    @Test
    void shouldStoreData() {
        assertDoesNotThrow(() -> {
            String fileName;

            try (XSSFWorkbook book = new XSSFWorkbook()) {
                XSSFSheet sheet = book.createSheet("Local");

                sheet.setDefaultColumnWidth(15);
                sheet.createRow(3);

                var pathD = Path.of("data/record");

                Path localate = this.pathBuilder.buildPath(pathD, "Localate");

                var request = this.excelFileCreatorService.saveWorkBook(book, localate);

                boolean result = request.get();

                assertTrue(result);

                File file = localate.toFile();
                assertTrue(file.exists());

                file.deleteOnExit();
            }
        });
    }

}