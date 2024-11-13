package org.flintcore.excel_expenses.excels_handler.serializable;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.files.extensions.ESerializableExtension;
import org.flintcore.excel_expenses.excels_handler.files.paths.FilePathHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;

@Component
@Log4j2
public class SerializableFileCreator {

    /**
     * Create a File to store or use Serializable objects.
     * <p>If default path does not end with any {@link ESerializableExtension serial extension},
     * it will add the {@link ESerializableExtension#getDefault() default} extension.</p>
     * <p>If contains any other path it won't override the file extension.</p>
     */
    public Optional<File> createSerializeFile(FilePathHolder fileHolder) throws FileAlreadyExistsException {
        File file = new File(fileHolder.asFullStringPath());
        try {
            this.createFile(file);
            return Optional.of(file);
        } catch (FileAlreadyExistsException e) {
            return Optional.empty();
        }
    }

    public void createFile(final File file) throws FileAlreadyExistsException {
        try {
            // Create parent directories if they don't exist
            Path parentPath = Path.of(file.getParent());
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                log.error("Failed to create directories: {}", e.getMessage());
            }

            // Check if file already exists
            Path filePath = Path.of(file.getPath());
            if (Files.exists(filePath)) {
                log.info("File already exists: {}", filePath);
                return;
            }

            // Create the file
            try {
                Files.createFile(filePath);
                log.info("Created file: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to create file: {}", e.getMessage());
            }
        } catch (InvalidPathException e) {
            log.error("Invalid path provided: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("Security violation: {}", e.getMessage());
        }
    }
}
