package org.flintcore.excel_expenses.excels_handler.utils.paths;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

@Component
@Log4j2
public class PathValidator {
    public static final String FILE_SUFFIX_PATTERN = ".+\\..+";

    public boolean isDirectory(Path path) {
        Path lastPath = path.getName(path.getNameCount() - 1);

        return !lastPath.toString().matches(FILE_SUFFIX_PATTERN);
    }

    public boolean isFile(Path path) {
        Path lastPath = path.getName(path.getNameCount() - 1);

        return lastPath.toString().matches(FILE_SUFFIX_PATTERN);
    }
}
