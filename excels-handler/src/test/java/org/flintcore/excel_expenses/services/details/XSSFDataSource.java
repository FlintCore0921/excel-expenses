package org.flintcore.excel_expenses.services.details;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class XSSFDataSource {
    public static Stream<Arguments> sheetWithTablesArgs() {
        return IntStream.range(4, 10)
                .mapToObj(val -> Arguments.of(val, (int) (val * 1.5)));
    }
}
