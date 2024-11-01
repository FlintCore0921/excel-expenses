package org.flintcore.excel_expenses.files.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SerializableExtensionUtilsTest {

    public static final ESerializableExtension EXTENSION = ESerializableExtension.getDefault();

    @Test
    void buildPath() {
        String[] path = {"local", "package", "file"};
        String newPath = SerializableExtensionUtils.buildPathOf(path, EXTENSION);

        System.out.println(newPath);

        assertTrue(Arrays.stream(path).allMatch(newPath::contains));
        assertFalse(newPath.endsWith(File.separator));
        assertTrue(newPath.endsWith(EXTENSION.suffixExtension()));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9})
    void buildPathWithAnExtensionProvided(int extensionSize) {
        String customExtension = UUID.randomUUID().toString().substring(0, extensionSize);

        String fileName = "file";
        String fileNameWithExtension = fileName + "." + customExtension;
        String[] path = {"local", "package", fileNameWithExtension};

        String newPath = SerializableExtensionUtils.buildPathOf(path, EXTENSION);

        // Print result
        System.out.println(newPath);

        for (String parentPath : Arrays.copyOf(path, path.length - 1)) {
            assertTrue(newPath.contains(parentPath), "path does not contains " + parentPath);
        }

        assertFalse(newPath.endsWith(File.separator),
                "path ends with " + File.separator);
        assertFalse(newPath.endsWith(customExtension),
                "path ends with wrong extension: " + customExtension);
        assertTrue(newPath.endsWith(EXTENSION.suffixExtension()),
                "path not ends with correct extension.");

        String suffixName = fileName + EXTENSION.suffixExtension();
        assertTrue(newPath.endsWith(suffixName),
                "Path does not ends with file name and extension: " + suffixName);
    }
}