package org.flintcore.excel_expenses.files.receipts;

import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.serializable.SerializeReader;
import org.flintcore.excel_expenses.serializable.SerializeWriter;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;

/**
 * File manager to save and get data from file of {@link Receipt} list on a specific Month and Year.
 * <p>
 * {@link PeriodReceiptSerializeFileManager#onMonth Month} and {@link PeriodReceiptSerializeFileManager#onMonth year}
 * are the current time is not set or defined.
 * </p>
 */
@Component
@Setter
public class PeriodReceiptSerializeFileManager extends ReceiptSerializeFileManager<Receipt> {
    private Month onMonth;
    private int onYear;

    public PeriodReceiptSerializeFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }

    @Override
    protected String[] getFilePath() {
        Month currentMonth = (onMonth = ObjectUtils.defaultIfNull(onMonth, LocalDate.now().getMonth()));
        int currentYear = onYear >= 0 ? onYear : (onYear = LocalDate.now().getYear());

        String fileName = "receipts_%s_%d".formatted(currentMonth, currentYear);
        return ArrayUtils.add(RECEIPTS_PATH, fileName);
    }
}
