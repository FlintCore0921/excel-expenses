package org.flintcore.utilities.dates;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateUtils {
    private DateUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant());
    }
}
