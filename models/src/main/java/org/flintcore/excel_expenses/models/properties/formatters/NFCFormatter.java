package org.flintcore.excel_expenses.models.properties.formatters;

import javafx.scene.control.TextFormatter;
import lombok.NonNull;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class NFCFormatter extends TextFormatter<String> {
    public NFCFormatter(@NonNull Supplier<String> formatterSupplier) {
        super(new Validator(formatterSupplier));
    }

    protected record Validator(Supplier<String> formatterSupplier) implements UnaryOperator<Change> {
        @Override
        public Change apply(Change change) {
            String newValue = change.getControlNewText();
            String format = this.formatterSupplier.get();

            if (!newValue.matches(format)) return null;

            return change;
        }
    }
}
