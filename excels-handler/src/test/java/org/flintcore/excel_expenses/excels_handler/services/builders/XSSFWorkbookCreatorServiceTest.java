package org.flintcore.excel_expenses.excels_handler.services.builders;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.excels_handler.TestBeanConfiguration;
import org.flintcore.excel_expenses.excels_handler.utils.XFFSExcelPathBuilder;
import org.flintcore.excel_expenses.excels_handler.utils.files.FileCreator;
import org.flintcore.excel_expenses.excels_handler.utils.paths.PathValidator;
import org.flintcore.excelib.commons.executors.DefaultThreadPoolHolder;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
        XSSFWorkbookCreatorService.class,
        XSSFFileService.class,
        PathValidator.class,
        FileCreator.class,
        XFFSExcelPathBuilder.class,
        DefaultThreadPoolHolder.class
})
class XSSFWorkbookCreatorServiceTest {
    @Autowired
    private XSSFWorkbookCreatorService XSSFCreatorService;
    @Autowired
    private XFFSExcelPathBuilder pathBuilder;

    @Test
    void shouldStoreData() {
        assertDoesNotThrow(() -> {
            try (XSSFWorkbook book = new XSSFWorkbook()) {
                XSSFSheet sheet = book.createSheet("Local");

                sheet.setDefaultColumnWidth(15);
                sheet.createRow(3);

                var pathD = Path.of("data/record");

                Path localate = this.pathBuilder.buildPath(pathD, "Localate");

                var request = this.XSSFCreatorService.saveWorkBook(book, localate);

                boolean result = request.get();

                assertTrue(result);

                File file = localate.toFile();
                assertTrue(file.exists());

                file.deleteOnExit();
            }
        });
    }

}