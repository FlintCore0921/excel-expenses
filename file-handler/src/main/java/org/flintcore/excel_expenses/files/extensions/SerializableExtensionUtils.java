package org.flintcore.excel_expenses.files.extensions;

import org.apache.commons.lang3.RegExUtils;
import org.flintcore.excel_expenses.files.paths.FilePathHolder;

import java.io.File;
import java.util.Arrays;

public final class SerializableExtensionUtils {
    private SerializableExtensionUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static boolean containsSerializableFilePath(final String path) {
        return Arrays.stream(ESerializableExtension.values()).anyMatch(
                ext -> path.endsWith(ext.suffixExtension())
        );
    }

    /**
     * Build the path to string.
     */
    public static String buildPath(FilePathHolder holder) {
        return buildPathOf(holder.paths(), holder.extension());
    }

    /**
     * Build the path to string.
     */
    public static String buildPathOf(final String[] paths, ESerializableExtension extension) {
        String[] copiedPath = Arrays.copyOf(paths, paths.length);

        String lastIndex = copiedPath[copiedPath.length - 1];
        lastIndex = RegExUtils.removePattern(lastIndex, "\\..*");

        copiedPath[copiedPath.length - 1] = lastIndex;

        return String.join(File.separator, copiedPath) + extension.suffixExtension();
    }
}
