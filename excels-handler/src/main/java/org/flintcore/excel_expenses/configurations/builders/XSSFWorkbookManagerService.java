package org.flintcore.excel_expenses.configurations.builders;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.tasks.SaveWorkBookFileTask;
import org.flintcore.excel_expenses.utils.files.FileCreator;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Service to handles Excel files from 2007 and later.
 */
@Log4j2
@Service
@Lazy
public class XSSFWorkbookManagerService {
    @Lazy
    protected final XSSFFileService workbookService;
    protected final FileCreator fileCreator;

    public XSSFWorkbookManagerService(
            XSSFFileService workbookService,
            FileCreator fileCreator
    ) {
        this.workbookService = workbookService;
        this.fileCreator = fileCreator;
    }

    // Set scheduled
    public Future<XSSFWorkbook> loadWorkBook(final String workbookName) {
        return loadWorkBook(Path.of(workbookName));
    }

    public Future<XSSFWorkbook> loadWorkBook(final Path workbookPath) {
        log.debug("Loading workbook:\n{}", workbookPath.toAbsolutePath());
        return this.workbookService.buildWorkBookFrom(workbookPath);
    }

    public Future<XSSFWorkbook> createEmptyWorkBook() {
        return this.workbookService.createWorkBookFile();
    }

    public Future<Boolean> saveWorkBook(
            final XSSFWorkbook workbook,
            final Path workbookLocation
    ) {
        log.debug("Saving workbook to: \n{}", workbookLocation);
        return CompletableFuture.supplyAsync(() -> this.fileCreator.createParentDirectory(workbookLocation))
                .thenComposeAsync(wasCreated ->
                        CompletableFuture.completedFuture(workbookLocation)
                ).thenApplyAsync(pathOptional ->
                        new SaveWorkBookFileTask(pathOptional, workbook).get()
                );
    }
}
