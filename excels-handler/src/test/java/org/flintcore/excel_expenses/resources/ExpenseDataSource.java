package org.flintcore.excel_expenses.resources;

import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ExpenseDataSource {
    public static Stream<Arguments> receiptDataArgumentsSource() {
        return Stream.of(
                Arguments.of(receiptListDataSource())
        );
    }

    public static List<Receipt> receiptListDataSource(){
        return List.of(
                new Receipt(
                        "990", new Date(),
                        new LocalBusiness("Local", "300"),
                        300.0, 40.0, 0.0
                ),
                new Receipt(
                        "990", new Date(),
                        new LocalBusiness("Local", "300"),
                        1300.0, 700.0, 0.0
                )
        );
    }

    public static Stream<Arguments> fileLocations() {
        return Stream.of("test_expenses.xlsx").map(Arguments::of);
    }
}
