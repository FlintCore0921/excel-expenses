package org.flintcore.excel_expenses.files.business;

import org.apache.commons.lang3.ArrayUtils;
import org.flintcore.excel_expenses.files.FileManager;
import org.flintcore.excel_expenses.files.SerializeReader;
import org.flintcore.excel_expenses.files.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.springframework.stereotype.Component;

@Component
public class LocalBusinessFileManager extends FileManager<LocalBusiness> {
    protected static final String[] BUSINESS_FILE_PATH = {"app", "storage", "business"};

    public LocalBusinessFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }

    @Override
    protected String[] getFilePath() {
        return ArrayUtils.add(BUSINESS_FILE_PATH, "records");
    }
}
