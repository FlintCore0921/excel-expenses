package org.flintcore.excel_expenses.excels_handler.properties;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;

@Component
@Setter
public class ExcelMonthNameBuilder extends ExcelNameBuilder {
    private static final String MONTH_FORMAT = "%s-%s";

    private Month month;

    public ExcelMonthNameBuilder() {
        super(MONTH_FORMAT);
    }

    @Override
    public String buildName() {
        String monthCapitalized = StringUtils.capitalize(month.name().toLowerCase());
        return this.format.formatted(this.name, monthCapitalized);
    }

    /** Set the month to the current month by {@link LocalDate}.*/
    public void setMonth() {
        this.setMonth(LocalDate.now().getMonth());
    }
}
