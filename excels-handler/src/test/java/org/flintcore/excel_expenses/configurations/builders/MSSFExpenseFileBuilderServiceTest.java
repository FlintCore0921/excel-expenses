package org.flintcore.excel_expenses.configurations.builders;

import org.apache.poi.ss.usermodel.Workbook;
import org.flintcore.excel_expenses.expenses.ExpenseBuilderHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.resources.PathResource;
import org.flintcore.excel_expenses.utils.files.ExpenseAreaReferenceBuilder;
import org.flintcore.excel_expenses.utils.files.FileCreator;
import org.flintcore.excel_expenses.utils.paths.PathValidator;
import org.flintcore.excelib.commons.executors.DefaultThreadPoolHolder;
import org.flintcore.excelib.services.builders.MSSFFileService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@SpringBootTest(classes = {
        MSSFExpenseFileBuilderService.class,
        MSSFWorkbookCreatorService.class,
        ExpenseAreaReferenceBuilder.class,
        MSSFFileService.class,
        DefaultThreadPoolHolder.class,
        FileCreator.class,
        PathValidator.class,
        PathResource.class
})
@ActiveProfiles({"test"})
class MSSFExpenseFileBuilderServiceTest {

    @Autowired
    private PathResource pathResource;
    @Autowired
    private MSSFExpenseFileBuilderService service;

    @Disabled
    @ParameterizedTest
    @MethodSource("org.flintcore.excel_expenses.excels_handler.resources.ExpenseDataSource#receiptDataArgumentsSource")
    void shouldLoadExpenseWorkBook(List<Receipt> listReceipt) {
        assumeFalse(Objects.isNull(service));
        assumeFalse(Objects.isNull(pathResource));

        assertDoesNotThrow(() -> {
            var filePath = Path.of(pathResource.getExternalExpensePath());

            var dataHolder = new ExpenseBuilderHolder(
                    filePath, "Reporte de Gastos", "money",
                    listReceipt
            );

            var expenseFileOptional = service.buildXSSFExpenseFile(dataHolder);

            var holderPair = expenseFileOptional.get();

            assertNotNull(holderPair);

            assertTrue(holderPair.isPresent());

            Workbook dataKey = holderPair.get().getKey();

            assertNotNull(dataKey);

            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                dataKey.write(outputStream);
            }
        });
    }
}