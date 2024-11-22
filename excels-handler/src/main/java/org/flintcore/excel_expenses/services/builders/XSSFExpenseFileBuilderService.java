package org.flintcore.excel_expenses.services.builders;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.expenses.ExpenseBuilderHolder;
import org.flintcore.excel_expenses.tasks.*;
import org.flintcore.excel_expenses.utils.files.ExpenseAreaReferenceBuilder;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Lazy
@Log4j2
@RequiredArgsConstructor
public class XSSFExpenseFileBuilderService {
    private final XSSFWorkbookManagerService workbookCreatorService;
    @Lazy
    private final ExpenseAreaReferenceBuilder areaReferenceBuilder;

    public Future<Optional<XSSFWorkbook>> buildXSSFExpenseFile(
            ExpenseBuilderHolder builderHolder
    ) {
        Path workbookPath = builderHolder.filePath();

        CompletableFuture<XSSFWorkbook> workBookCreationFuture = FutureHandlerUtils.asCompletable(
                Objects.nonNull(workbookPath) ? // If path not provided just create one.
                        this.workbookCreatorService.loadWorkBook(workbookPath) :
                        this.workbookCreatorService.createEmptyWorkBook()
        );

        var receiptData = builderHolder.receipts();

        return workBookCreationFuture.thenApply( // Create the sheet
                        wb -> new PrepareXSSFSheetTask(wb)
                                .apply(builderHolder.sheetName())
                ).thenApply(sheet -> { // Create the table
                    XSSFWorkbook workbook = sheet.getWorkbook();

                    var tableArea = areaReferenceBuilder.buildAreaReference(
                            sheet, builderHolder.receipts().size(),
                            workbook.getSpreadsheetVersion()
                    );

                    return new PrepareXSSFTableTask(tableArea)
                            .apply(sheet, builderHolder.tableName());
                }).thenApply(new XSSFApplyExpenseHeaderTask()) // Add the header columns at the start of the table
                .thenApply(new ApplyMediumStyleTask())
                .thenApply(tb -> new LoadExpenseDataOnTableTask<>()
                        .apply(tb, receiptData)
                ).handleAsync((result, thr) -> {
                    try {
                        if (Objects.nonNull(thr) || !result) throw new RuntimeException();
                        return workBookCreationFuture.get();
                    } catch (Throwable e) {
                        return null;
                    }
                })
                .thenApply(Optional::ofNullable);
    }
}
