package org.flintcore.excel_expenses.managers.filters;

import javafx.util.StringConverter;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BasicBusinessStringConverter<T extends IBusiness> extends StringConverter<T> {
    private final Supplier<List<T>> dataSupplier;
    private final String separator;

    public BasicBusinessStringConverter(Supplier<List<T>> dataSupplier) {
        this.dataSupplier = dataSupplier;
        this.separator = "-";
    }

    @Override
    public String toString(IBusiness obj) {
        return Objects.nonNull(obj) ? "%s - %s".formatted(obj.getRNC(), obj.getName()) : "";
    }

    @Override
    public T fromString(String displayText) {
        List<T> businesses = dataSupplier.get();
        T business = extractFrom(displayText);

        return businesses.stream()
                .filter(b -> b.compareTo(business) == 0)
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private T extractFrom(String data) {
        String[] parts = data.split(separator);
        return (T) new LocalBusiness(parts[1].trim(), parts[0].trim());
    }
}
