package org.flintcore.excel_expenses.serializable;

import org.flintcore.excel_expenses.files.extensions.ESerializableExtensions;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

@Component
public class SerializableFileFinder {

    /**
     * Create a File to store or use Serializable objects.
     */
    public Optional<File> getSerializeFile(String... paths) {
        String fullPath = String.join(File.separator, paths);

        if (Arrays.stream(ESerializableExtensions.values())
                .noneMatch(ext ->  fullPath.endsWith(ext.suffixExtension()))
        ) {
            return Optional.empty();
        }

        File file = new File(fullPath);

        if (!file.exists()) {
            return Optional.empty();
        }

        return Optional.of(file);
    }
}
