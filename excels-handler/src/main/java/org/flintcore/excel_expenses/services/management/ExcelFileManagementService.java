package org.flintcore.excel_expenses.services.management;

import lombok.Getter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

@Service
@Lazy
public class ExcelFileManagementService {
    private final static long MAX_TIME_GET_FILE = 1L; // Minutes
    protected final Logger logger;

    private ReentrantReadWriteLock lock;
    private final ReadLock readLock;
    private final WriteLock writeLock;

    protected final XSSFFileService workbookService;
    protected String workbookName;
    @Getter
    protected XSSFWorkbook workbook;

    public ExcelFileManagementService(XSSFFileService workbookService) {
        this.workbookService = workbookService;

        this.logger = LoggerFactory.getLogger(ExcelFileManagementService.class);

        this.lock = new ReentrantReadWriteLock(true);
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    // Set scheduled
    public void loadWorkBook() {
        writeLock.lock();
        try {
            logger.debug("Loading workbook %s...".formatted(workbookName));
            workbook = this.workbookService.buildWorkBookFrom(Path.of(workbookName))
                    .get(MAX_TIME_GET_FILE, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Unable to get excel file by the default path.");
        }
        writeLock.unlock();
    }
}
