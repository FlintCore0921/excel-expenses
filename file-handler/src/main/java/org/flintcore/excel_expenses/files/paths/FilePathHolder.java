package org.flintcore.excel_expenses.files.paths;

import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.flintcore.excel_expenses.files.extensions.ESerializableExtension;

public record FilePathHolder(
        @NonNull String[] paths,
        @NonNull ESerializableExtension extension
) {
    /** An array with the file to be handled easily.*/
    public String[] fullPath() {
        return ArrayUtils.add(paths, extension.suffixExtension());
    }
}
