package org.flintcore.excel_expenses.services.excels;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.expenses.ExpenseBuilderHolder;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.excel_expenses.services.builders.XSSFExpenseFileBuilderService;
import org.flintcore.excel_expenses.services.builders.XSSFWorkbookManagerService;
import org.flintcore.excel_expenses.services.receipts.ReceiptFileScheduledFXService;
import org.springframework.context.annotation.Lazy;

import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Lazy
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Log4j2
public class XSSFExcelExpenseExportFXService extends Service<Boolean> {

    private static final String NO_PATH_LOCATION_PROVIDED = "No path location provided.";
    private static final String NO_DATA_PROVIDED = "No data provided.";
    private static final String NO_EXCEL_FILE_PROVIDED = "No excel file provided.";
    private static final String FILE_SAVED_SUCCESSFULLY = "File saved successfully.";

    private final ReceiptFileScheduledFXService receiptFileService;
    private final XSSFExpenseFileBuilderService excelFileBuilderService;
    private final XSSFWorkbookManagerService workbookBuilderManager;

    @Setter
    private ExpenseBuilderHolder expenseFileHolder;

    @Setter
    private Path newExpenseLocation;

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Get the data to store.
                    List<Receipt> receipts = List.copyOf(
                            receiptFileService.getDataList().get()
                    );

                    // If empty, just send a message and abort.
                    if (receipts.isEmpty()) {
                        updateMessage(NO_DATA_PROVIDED);
                        log.info(NO_DATA_PROVIDED);
                        return Boolean.FALSE;
                    }

                    // If file path is not provided then omit.
                    if (Objects.isNull(newExpenseLocation)) {
                        updateMessage(NO_PATH_LOCATION_PROVIDED);
                        log.info(NO_PATH_LOCATION_PROVIDED);
                        return Boolean.FALSE;
                    }

                    // Request create new File
                    var workBookFutureData = excelFileBuilderService.buildXSSFExpenseFile(
                            expenseFileHolder
                    ).get();

                    // Prepare book to be saved
                    try (var workBookData = workBookFutureData.orElseThrow()) {
                        boolean result = workbookBuilderManager.saveWorkBook(
                                workBookData, newExpenseLocation
                        ).get();

                        // If save process fails, just send message and abort.
                        if (!result) {
                            updateMessage("Expense file could not be saved.");
                            return Boolean.FALSE;
                        }

                    } catch (NoSuchElementException ex) {
                        updateMessage(NO_EXCEL_FILE_PROVIDED);
                        return Boolean.FALSE;
                    }

                    // TODO Use bundle message.
                    updateMessage(FILE_SAVED_SUCCESSFULLY);
                    return Boolean.TRUE;
                } catch (InterruptedException | ExecutionException | NullPointerException e) {
                    log.error(e.getMessage(), e);
                }

                return Boolean.FALSE;
            }
        };
    }
}
