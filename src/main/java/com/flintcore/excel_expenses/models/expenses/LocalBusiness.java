package com.flintcore.excel_expenses.models.expenses;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

@Builder
public record LocalBusiness(
        String name,
        String RNC
) implements IBusiness, Serializable, Comparable<LocalBusiness> {
    @Serial
    private static final long serialVersionUID = 9938242L;

    @Override
    public int compareTo(@NonNull LocalBusiness o) {
        return Comparator.comparing(LocalBusiness::getRNC)
                .thenComparing(LocalBusiness::getName)
                .compare(this, o);
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
