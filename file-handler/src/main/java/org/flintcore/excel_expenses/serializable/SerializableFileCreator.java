package org.flintcore.excel_expenses.serializable;

import org.flintcore.excel_expenses.files.extensions.ESerializableExtensions;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

@Component
public class SerializableFileCreator {

    /**
     * Create a File to store or use Serializable objects.
     */
    public File createSerializeFile(String... paths) throws FileAlreadyExistsException {
        final String fullPath = String.join(File.separator, paths);

        String finalFullPath = fullPath;
        if (Arrays.stream(ESerializableExtensions.values())
                .noneMatch(ext -> fullPath.endsWith(ext.suffixExtension()))) {
            finalFullPath += ESerializableExtensions.DAT.suffixExtension();
        }

        File file = new File(finalFullPath);

        if (file.exists()) {
            throw new FileAlreadyExistsException(fullPath);
        }

        try {
            if (!file.getParentFile().mkdirs() || !file.createNewFile()) {
                throw new IOException();
            }
        } catch (IOException e) {
            return null;
        }

        return file;
    }
}
