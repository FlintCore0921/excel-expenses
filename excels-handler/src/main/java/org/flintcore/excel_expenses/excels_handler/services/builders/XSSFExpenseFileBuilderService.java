package org.flintcore.excel_expenses.excels_handler.services.builders;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.excels_handler.expenses.ExpenseBuilderHolder;
import org.flintcore.excel_expenses.excels_handler.tasks.*;
import org.flintcore.excel_expenses.excels_handler.utils.files.ExpenseAreaReferenceBuilder;
import org.flintcore.excelib.commons.utilities.FutureHandlerUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Objects;
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

    public Future<Pair<Workbook, ExpenseBuilderHolder>> buildXSSFExpenseFile(
            ExpenseBuilderHolder builderHolder
    ) {
        Path workbookPath = builderHolder.filePath();

        CompletableFuture<XSSFWorkbook> completableFuture = FutureHandlerUtils.asCompletable(
                this.workbookCreatorService.loadWorkBook(workbookPath)
        );

        var receiptData = builderHolder.receipts();

        return completableFuture.thenApply( // Create the sheet
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
                        .apply(tb, receiptData))
                .handleAsync((result, thr) -> {
                    try {
                        if (Objects.nonNull(thr) || !result) throw new RuntimeException();
                        return Pair.ofNonNull(completableFuture.get(), builderHolder);
                    } catch (Throwable e) {
                        return Pair.of(null, null);
                    }
                });
    }
}