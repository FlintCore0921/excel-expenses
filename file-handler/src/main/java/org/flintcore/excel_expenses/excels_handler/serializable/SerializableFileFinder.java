package org.flintcore.excel_expenses.excels_handler.serializable;

import org.flintcore.excel_expenses.excels_handler.files.extensions.SerializableExtensionUtils;
import org.flintcore.excel_expenses.excels_handler.files.paths.FilePathHolder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class SerializableFileFinder {

    /**
     * Create a File to store or use Serializable objects.
     */
    public Optional<File> getSerializeFile(FilePathHolder fileHolder) {
        String fullPath = fileHolder.asFullStringPath();

        if (!SerializableExtensionUtils.containsSerializableFilePath(fullPath)) {
            return Optional.empty();
        }

        File file = new File(fullPath);

        if (!file.exists()) {
            return Optional.empty();
        }

        return Optional.of(file);
    }
}
