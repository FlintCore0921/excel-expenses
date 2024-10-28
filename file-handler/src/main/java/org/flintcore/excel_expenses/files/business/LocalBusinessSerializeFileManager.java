package org.flintcore.excel_expenses.files.business;

import org.apache.commons.lang3.ArrayUtils;
import org.flintcore.excel_expenses.serializable.SerializeFileManager;
import org.flintcore.excel_expenses.serializable.SerializeReader;
import org.flintcore.excel_expenses.serializable.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.springframework.stereotype.Component;

@Component
public class LocalBusinessSerializeFileManager extends SerializeFileManager<LocalBusiness> {
    protected static final String[] BUSINESS_FILE_PATH = {"app", "storage", "business"};

    public LocalBusinessSerializeFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }

    @Override
    protected String[] getFilePath() {
        return ArrayUtils.add(BUSINESS_FILE_PATH, "local_record");
    }
}
