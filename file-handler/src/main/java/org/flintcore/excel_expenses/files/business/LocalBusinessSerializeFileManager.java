package org.flintcore.excel_expenses.files.business;

import org.apache.commons.lang3.ArrayUtils;
import org.flintcore.excel_expenses.files.paths.FilePathHolder;
import org.flintcore.excel_expenses.serializable.SerializeFileManager;
import org.flintcore.excel_expenses.serializable.SerializeReader;
import org.flintcore.excel_expenses.serializable.SerializeWriter;
import org.flintcore.excel_expenses.models.business.LocalBusiness;
import org.springframework.stereotype.Component;

@Component
public class LocalBusinessSerializeFileManager extends SerializeFileManager<LocalBusiness> {
    protected static final String[] BUSINESS_FILE_PATH = {"app", "storage", "business"};
    // File name
    public static final String LOCAL_RECORD = "local_records";

    public LocalBusinessSerializeFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }

    @Override
    protected FilePathHolder getFilePath() {
        String[] file_path = ArrayUtils.add(BUSINESS_FILE_PATH, LOCAL_RECORD);
        return new FilePathHolder(file_path, DEFAULT_SERIAL_EXTENSION);
    }
}
