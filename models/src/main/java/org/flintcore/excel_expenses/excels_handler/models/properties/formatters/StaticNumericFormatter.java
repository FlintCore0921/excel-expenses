package org.flintcore.excel_expenses.excels_handler.models.properties.formatters;

import data.utils.NullableUtils;
import javafx.scene.control.TextFormatter;
import lombok.RequiredArgsConstructor;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Fixed text length at create.
 */
public class StaticNumericFormatter extends TextFormatter<String> {
    public StaticNumericFormatter(int length, int precision) {
        super(new Validator(length, precision));
    }

    public StaticNumericFormatter(int precision) {
        super(new Validator(-1, precision));
    }

    @RequiredArgsConstructor
    protected static class Validator implements UnaryOperator<Change> {
        private final int length, precision;
        private Pattern formatPattern;

        @Override
        public Change apply(Change change) {
            var newValue = change.getControlNewText();

            if (!isValidContent(newValue)) {
                return null;
            }

            return change;
        }

        private boolean isValidContent(String value) {
            initPattern();
            return formatPattern.matcher(value).matches();
        }

        private void initPattern() {
            NullableUtils.executeIsNull(this.formatPattern, () -> {
                String fixedLengthText = length < 0 ? "*" : "{0,%s}".formatted(Integer.toString(length));
                String pattern = "^\\d%s(\\.\\d{0,%d})?$".formatted(fixedLengthText, precision);
                this.formatPattern = Pattern.compile(pattern);
            });
        }
    }
}
