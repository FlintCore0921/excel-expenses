package org.flintcore.excel_expenses.files.paths;

import lombok.NonNull;
import org.flintcore.excel_expenses.files.extensions.ESerializableExtension;
import org.flintcore.excel_expenses.files.extensions.SerializableExtensionUtils;

public record FilePathHolder(
        @NonNull String[] paths,
        @NonNull ESerializableExtension extension
) {
    /**
     * Build the full file path as a string.
     * <p>Same to call {@link SerializableExtensionUtils#buildPath(FilePathHolder)}.</p>
     */
    public String asFullStringPath() {
        return SerializableExtensionUtils.buildPath(this);
    }
}
