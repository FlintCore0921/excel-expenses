package org.flintcore.excel_expenses.models.expenses;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.util.Comparator;

@Builder
public record LocalBusiness(
        String name,
        String RNC
) implements IBusiness {
    @Serial
    private static final long serialVersionUID = 9938242L;

    @Override
    public int compareTo(@NonNull IBusiness o) {
        return Comparator.comparing(IBusiness::getRNC)
                .thenComparing(IBusiness::getName)
                .compare(this, o);
    }

    public int compareTo(@NonNull LocalBusiness o) {
        return this.compareTo((IBusiness) o);
    }

    @Override
    public String getRNC() {
        return RNC();
    }

    @Override
    public String getName() {
        return name();
    }
}
