package org.flintcore.excel_expenses.files.business;

import org.flintcore.excel_expenses.files.FileManager;
import org.flintcore.excel_expenses.files.SerializeReader;
import org.flintcore.excel_expenses.files.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.springframework.stereotype.Component;

@Component
public class LocalBusinessFileManager extends FileManager<LocalBusiness> {
    public static final String[] LOCAL_BUSINESS_FILE_PATH = {"app", "storage", "data", "localBusiness.txt"};

    public LocalBusinessFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(LOCAL_BUSINESS_FILE_PATH, serializeWriter, serializeReader);
    }
}
