package org.flintcore.excel_expenses.models.properties.formatters;

import javafx.scene.control.TextFormatter;

public class RNCFormatter extends TextFormatter<String> {
    public RNCFormatter(int maxSize) {
        super(change -> {
            if (change.getControlNewText().length() > maxSize) {
                return null;
            }

            return change;
        });
    }
}
