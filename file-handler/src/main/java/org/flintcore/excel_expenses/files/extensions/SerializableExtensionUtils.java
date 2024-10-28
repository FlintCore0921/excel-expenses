package org.flintcore.excel_expenses.files.extensions;

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
}
