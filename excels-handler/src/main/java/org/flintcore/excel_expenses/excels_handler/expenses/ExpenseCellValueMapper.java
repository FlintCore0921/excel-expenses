package org.flintcore.excel_expenses.excels_handler.expenses;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;
import org.flintcore.utilities.dates.DateUtils;

@Log4j2
public class ExpenseCellValueMapper {

    public <E extends Receipt> Pair<@NonNull EExpenseCellData, @NonNull ?> getCellValueOf(
            int columnIndex, E expense
    ) {
        EExpenseCellData key = EExpenseCellData.values()[columnIndex];

        var value = switch (key) {
            case DATE -> DateUtils.convertToLocalDate(expense.dateCreation());
            case LOCATION_NAME -> expense.business().getName();
            case RNC -> expense.business().getRNC();
            case NCF -> expense.NFC();
            case NON_ITBIS_AMOUNT -> expense.price();
            case ITBIS_AMOUNT -> expense.itbPrice();
            case SERVICE_AMOUNT -> expense.servicePrice();
            case TOTAL_AMOUNT -> expense.getTotalPrice();
        };

        return Pair.of(key, value);
    }
}
