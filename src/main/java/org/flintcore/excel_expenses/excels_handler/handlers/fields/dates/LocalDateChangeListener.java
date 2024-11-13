package org.flintcore.excel_expenses.excels_handler.handlers.fields.dates;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.util.function.Consumer;

public class LocalDateChangeListener implements ChangeListener<LocalDate> {
    private final Consumer<LocalDate> consumer;

    public LocalDateChangeListener(Consumer<LocalDate> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
        this.consumer.accept(newValue);
    }
}
