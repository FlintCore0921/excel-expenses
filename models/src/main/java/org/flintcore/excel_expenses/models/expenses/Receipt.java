package org.flintcore.excel_expenses.models.expenses;

import lombok.Builder;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Date;

@Builder
public record Receipt(
        String NFC,
        double price,
        double servicePrice,
        double itbPrice,
        Date dateCreation,
        IBusiness business
) implements Serializable, Comparable<Receipt> {
    @Serial
    private static final long serialVersionUID = 64621238L;
    public static final int RECEIPT_SCALE = 2;

    public Double getTotalPrice() {
        return BigDecimal.valueOf(price)
                .add(BigDecimal.valueOf(servicePrice))
                .add(BigDecimal.valueOf(itbPrice))
                .setScale(RECEIPT_SCALE, RoundingMode.HALF_DOWN)
                .doubleValue();
    }

    @Override
    public int compareTo(@NonNull Receipt o) {
        return Comparator.comparing(Receipt::NFC)
                .thenComparing(Receipt::dateCreation)
                .thenComparing(Receipt::business)
                .compare(this, o);
    }
}
