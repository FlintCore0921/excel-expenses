package org.flintcore.excel_expenses.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

// Requires
@Log4j2
@RequiredArgsConstructor
public class SaveWorkBookFileTask implements Supplier<Boolean> {
    private final Path location;
    private final Workbook workbook;

    @Override
    public Boolean get() {
        File absolutePathFile = location.toAbsolutePath().toFile();

        try (FileOutputStream fileOutputStream = new FileOutputStream(absolutePathFile)) {
            workbook.write(fileOutputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
