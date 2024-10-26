package org.flintcore.excel_expenses.files.receipts;

import org.flintcore.excel_expenses.files.FileManager;
import org.flintcore.excel_expenses.files.SerializeReader;
import org.flintcore.excel_expenses.files.SerializeWriter;
import org.flintcore.excel_expenses.models.receipts.Receipt;

public abstract class ReceiptFileManager<T extends Receipt> extends FileManager<T> {
    protected static final String[] RECEIPTS_PATH = {"app", "storage", "receipts"};

    public ReceiptFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }
}
