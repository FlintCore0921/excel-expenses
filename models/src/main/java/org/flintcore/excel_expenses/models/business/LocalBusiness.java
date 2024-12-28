package org.flintcore.excel_expenses.models.business;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.util.Comparator;
import java.util.Objects;

// TODO Test las services.
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
    public boolean equals(Object obj) {
        return this == obj || obj instanceof IBusiness business
                && Objects.equals(this.RNC, business.getRNC());
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
