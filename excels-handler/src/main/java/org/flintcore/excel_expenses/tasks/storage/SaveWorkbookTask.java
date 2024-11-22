package org.flintcore.excel_expenses.tasks.storage;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;

import java.nio.file.Path;
@RequiredArgsConstructor
public class SaveWorkbookTask implements Runnable {
    private final Workbook workbook;
    private final Path path;

    @Override
    public void run() {

    }


}
