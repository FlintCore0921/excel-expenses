package org.flintcore.utilities.dates;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;

public final class DateUtils {
    private DateUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Date convertToDate(LocalDate localDate) {
        if (Objects.isNull(localDate)) return null;
        return Date.from(localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant());
    }
}
