package com.flintcore.excel_expenses.models.expenses;

import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public record Receipt(
        String NFC,
        Double price,
        Date dateCreation,
        IBusiness business
) implements Serializable, Comparable<Receipt> {
    @Serial
    private static final long serialVersionUID = 64621238L;

    @Override
    public int compareTo(@NonNull Receipt o) {
        return Comparator.comparing(Receipt::NFC)
                .thenComparing(Receipt::dateCreation)
                .compare(this, o);
    }
}
