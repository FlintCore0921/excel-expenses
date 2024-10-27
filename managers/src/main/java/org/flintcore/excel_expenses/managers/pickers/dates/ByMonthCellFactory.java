package org.flintcore.excel_expenses.managers.pickers.dates;

import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.Month;

public class ByMonthCellFactory implements Callback<DatePicker, DateCell> {
    @Override
    public DateCell call(DatePicker param) {
        return new MCell();
    }

    protected static class MCell extends DateCell {
        @Override
        public void updateItem(LocalDate item, boolean empty) {
            super.updateItem(item, empty);
            setEnabled(item);
        }

        private void setEnabled(LocalDate item) {
            LocalDate currentDate = LocalDate.now();

            Month currentMonth = currentDate.getMonth();
            int currentYear = currentDate.getYear();

            boolean isDisabled = item.getMonth().equals(currentMonth)
                    && item.getYear() == currentYear;

            setDisable(!isDisabled);
        }
    }
}
