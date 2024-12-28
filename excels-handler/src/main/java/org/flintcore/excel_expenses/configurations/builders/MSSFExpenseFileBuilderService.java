package org.flintcore.excel_expenses.configurations.builders;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
// TODO Create abstract.
// TODO handle file better / test failed.
public class MSSFExpenseFileBuilderService {
    private final MSSFWorkbookCreatorService workbookCreatorService;
    @Lazy
    private final ExpenseAreaReferenceBuilder areaReferenceBuilder;

    public Future<Optional<Pair<Workbook, ExpenseBuilderHolder>>> buildXSSFExpenseFile(ExpenseBuilderHolder builderHolder) {
        Path workbookPath = builderHolder.filePath();

        CompletableFuture<Workbook> completableFuture = FutureHandlerUtils.asCompletable(
                this.workbookCreatorService.loadWorkBook(workbookPath)
        );

        var receiptData = builderHolder.receipts();

        return completableFuture.thenApply(wb -> { // Create the sheet
                    if (wb instanceof XSSFWorkbook xwb) return new PrepareXSSFSheetTask(xwb)
                            .apply(builderHolder.sheetName());

                    if (wb instanceof HSSFWorkbook hwb) return new PrepareHSSFSheetTask(hwb)
                            .apply(builderHolder.sheetName());

                    // Checks later
                    throw new RuntimeException();
                }).thenApply(sheet -> { // Create the table
                    Workbook workbook = sheet.getWorkbook();

                    var tableArea = areaReferenceBuilder.buildAreaReference(
                            sheet, builderHolder.receipts().size(),
                            workbook.getSpreadsheetVersion()
                    );

                    if (sheet instanceof XSSFSheet xSheet) return new PrepareXSSFTableTask(tableArea)
                            .apply(xSheet, builderHolder.tableName());

                    // Checks later
                    throw new RuntimeException();
                }).thenApply(new XSSFApplyExpenseHeaderTask()) // Add the header columns at the start of the table
                .thenApply(new ApplyMediumStyleTask())
                .thenApply(tb -> new LoadExpenseDataOnTableTask<>()
                        .apply(tb, receiptData))
                .handleAsync((result, thr) -> {
                    try {
                        if (Objects.nonNull(thr) || !result) throw new RuntimeException();
                        return Pair.ofNonNull(completableFuture.get(), builderHolder);
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                }).thenApply(Optional::ofNullable);
    }
}
