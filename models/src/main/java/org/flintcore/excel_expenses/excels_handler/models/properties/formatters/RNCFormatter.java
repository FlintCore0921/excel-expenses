package org.flintcore.excel_expenses.excels_handler.models.properties.formatters;

import javafx.scene.control.TextFormatter;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;

public class RNCFormatter extends TextFormatter<String> {
    public RNCFormatter(int maxSize) {
        super(new Validator(maxSize));
    }

    @RequiredArgsConstructor
    protected static final class Validator implements UnaryOperator<Change> {
        private final int textLength;

        @Override
        public Change apply(Change change) {
            var newValueText = change.getControlNewText();

            if (newValueText.length() > this.textLength || !this.contentValid(newValueText)){
                return null;
            }

            return change;
        }

        // In this case, only numbers.
        private boolean contentValid(String text) {
            return text.matches("\\d*");
        }
    }
}
