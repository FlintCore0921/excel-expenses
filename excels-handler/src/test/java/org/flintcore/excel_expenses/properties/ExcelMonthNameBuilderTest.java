package org.flintcore.excel_expenses.properties;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ExcelMonthNameBuilder.class})
class ExcelMonthNameBuilderTest {
    public static final String EXCEL_SAVER = "excelSaver";
    @Autowired
    private ExcelMonthNameBuilder nameBuilder;

    private static Stream<Arguments> allMonthArgument() {
        return Arrays.stream(Month.values())
                .map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        this.nameBuilder.setName(EXCEL_SAVER);
    }

    @Test
    void shouldNameContainsCurrentMonth() {
        Month currentMonth = LocalDate.now().getMonth();
        String nameBuilt = this.nameBuilder.buildName();


        checkMonthInText(currentMonth, nameBuilt);
    }

    private void checkMonthInText(Month currentMonth, String nameBuilt) {
        String monthString = StringUtils.capitalize(currentMonth.name().toLowerCase());
        assertTrue(nameBuilt.contains(monthString));
    }

    @ParameterizedTest
    @MethodSource("allMonthArgument")
    void shouldNameContainsMonth(Month month) {
        this.nameBuilder.setMonth(month);
        String nameBuilt = this.nameBuilder.buildName();

        System.out.printf("File name: %s%n", nameBuilt);

        checkMonthInText(month, nameBuilt);
    }

    @Test
    void shouldSetCurrentMonth() {
        this.nameBuilder.setMonth();

        checkMonthInText(LocalDate.now().getMonth(), this.nameBuilder.buildName());
    }
}