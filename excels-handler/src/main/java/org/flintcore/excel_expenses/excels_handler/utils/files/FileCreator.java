package org.flintcore.excel_expenses.excels_handler.utils.files;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.utils.paths.PathValidator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public final class FileCreator {

    private final PathValidator pathValidator;

    public Optional<File> createFile(Path filePath) {
        if (!pathValidator.isFile(filePath)) {
            log.warn("The file must has an extension or not be a directory.");
            return Optional.empty();
        }

        var file = filePath.toFile();

        var fileCreated = file.exists() || createNewFile(file);

        return fileCreated ? Optional.of(file) : Optional.empty();
    }

    private boolean createNewFile(File file) {
        try {
            return file.mkdirs() && file.createNewFile();
        } catch (IOException e) {
            log.warn("Error creating new file", e);
            return false;
        }
    }


    public boolean createParentDirectory(@NonNull File file) {
        if (!pathValidator.isDirectory(file.getParentFile().toPath())) {
            log.warn("The file must be a directory.");
            return false;
        }

        try {
            return file.getParentFile().mkdirs() && file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean createParentDirectory(@NonNull Path filePath) {
        return createParentDirectory(filePath.toFile());
    }
}
