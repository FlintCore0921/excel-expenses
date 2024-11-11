package org.flintcore.excel_expenses.services.management;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.Future;

@Log4j2
@Service
@Lazy
public class ExcelFileManagementService {
    protected final XSSFFileService workbookService;

    public ExcelFileManagementService(XSSFFileService workbookService) {
        this.workbookService = workbookService;
    }

    // Set scheduled
    public Future<XSSFWorkbook> loadWorkBook(final String workbookName) {
        log.debug("Loading workbook:\n{}", workbookName);
        return this.workbookService.buildWorkBookFrom(Path.of(workbookName));
    }
}
