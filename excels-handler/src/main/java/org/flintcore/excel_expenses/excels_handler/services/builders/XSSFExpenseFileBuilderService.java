package org.flintcore.excel_expenses.excels_handler.services.builders;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.excels_handler.expenses.XSSFExpenseBuilderHolder;
import org.flintcore.excel_expenses.excels_handler.tasks.PrepareXSSFSheetTask;
import org.flintcore.excel_expenses.excels_handler.tasks.PrepareXSSFTableTask;
import org.flintcore.excel_expenses.excels_handler.utils.files.ExpenseAreaReferenceBuilder;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class XSSFExpenseFileBuilderService {
    private final XSSFWorkbookCreatorService workbookCreatorService;
    @Lazy
    private final ExpenseAreaReferenceBuilder areaReferenceBuilder;

    public Future<Path> buildXSSFExpenseFile(XSSFExpenseBuilderHolder builderHolder) {
        Path workbookPath = builderHolder.filePath();

        CompletableFuture<XSSFWorkbook> completableFuture = FutureHandlerUtils.asCompletable(
                this.workbookCreatorService.loadWorkBook(workbookPath)
        );

        return completableFuture.thenApply( // Create the sheet
                wb -> new PrepareXSSFSheetTask(wb).apply(builderHolder.sheetName())
        ).thenApply(sheet -> { // Create the table
            XSSFWorkbook workbook = sheet.getWorkbook();

            var tableArea = areaReferenceBuilder.buildAreaReference(
                    sheet, builderHolder.receipts().size(),
                    workbook.getSpreadsheetVersion()
            );

            return new PrepareXSSFTableTask(tableArea)
                    .apply(sheet, builderHolder.tableName());
        }).thenApply(table -> { // Load data in table
            return null;
        }).thenApply(__ -> workbookPath);
    }
}
