package org.flintcore.excel_expenses.configurations.builders;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.flintcore.excel_expenses.resources.PathResource;
import org.flintcore.excel_expenses.resources.XSSFUtils;
import org.flintcore.excel_expenses.utils.XFFSExcelPathBuilder;
import org.flintcore.excel_expenses.utils.files.FileCreator;
import org.flintcore.excel_expenses.utils.paths.PathValidator;
import org.flintcore.excelib.commons.executors.DefaultThreadPoolHolder;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        XSSFWorkbookManagerService.class,
        XSSFFileService.class,
        PathValidator.class,
        FileCreator.class,
        XFFSExcelPathBuilder.class,
        DefaultThreadPoolHolder.class,
        PathResource.class
})
@ActiveProfiles({"test"})
class XSSFWorkbookManagerServiceTest {
    @Autowired
    private XSSFWorkbookManagerService XSSFCreatorService;
    @Autowired
    private XFFSExcelPathBuilder pathBuilder;
    @Autowired
    private PathResource pathResource;

    @Test
    void shouldStoreData() {
        assertDoesNotThrow(() -> {
            XSSFUtils.handleEmptyWorkbook(book -> {
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
            });
        });
    }

    @Test
    void shouldLoadData() {
        assertDoesNotThrow(() -> {
            assertDoesNotThrow(() -> {
                var pathFileName = this.pathResource.getSecondaryExpensePath();

                var request = this.XSSFCreatorService.loadWorkBook(pathFileName);

                var requestResult = request.get();

                assertNotNull(requestResult);
            });
        });
    }

    @Test
    void shouldLoadFromExternalProject() {
        assertDoesNotThrow(() -> {
            var pathFileName = this.pathResource.getExternalExpensePath();

            var request = this.XSSFCreatorService.loadWorkBook(pathFileName);

            var requestResult = request.get();

            assertNotNull(requestResult);
        });
    }
}