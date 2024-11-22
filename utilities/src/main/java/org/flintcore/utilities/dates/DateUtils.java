package org.flintcore.utilities.dates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

public final class DateUtils {
    private DateUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Date convertToDate(LocalDate localDate) {
        if (Objects.isNull(localDate)) return null;
        return Date.from(localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant());
    }

    public static LocalDate convertToLocalDate(Date date) {
        if (Objects.isNull(date)) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Function<Date, String> createPatternOf(final String pattern) {
        final DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat::format;
    }

    public static Function<Temporal, String> createPatternOfTemporal(final String pattern) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormatter::format;
    }
}
