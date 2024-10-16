package org.flintcore.excel_expenses.handlers.filters;

import javafx.util.StringConverter;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class BusinessStringConverter extends StringConverter<IBusiness> {
    private final String separator;

    private final Supplier<List<IBusiness>> dataSupplier;

    public BusinessStringConverter(Supplier<List<IBusiness>> dataSupplier) {
        this.dataSupplier = dataSupplier;
        this.separator = "-";
    }

    @Override
    public String toString(IBusiness obj) {
        return Objects.nonNull(obj) ? "%s - %s".formatted(obj.getRNC(), obj.getName()) : "";
    }

    @Override
    public IBusiness fromString(String displayText) {
        List<IBusiness> businesses = dataSupplier.get();
        LocalBusiness business = extractFrom(displayText);

        return businesses.stream()
                .filter(b -> b.compareTo(business) == 0)
                .findFirst()
                .orElse(null);
    }

    private LocalBusiness extractFrom(String data) {
        String[] parts = data.split(separator);
        return new LocalBusiness(parts[1].trim(), parts[0].trim());
    }
}
