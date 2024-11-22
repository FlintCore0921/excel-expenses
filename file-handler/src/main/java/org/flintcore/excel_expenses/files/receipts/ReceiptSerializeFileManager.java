package org.flintcore.excel_expenses.files.receipts;

import org.flintcore.excel_expenses.serializable.SerializeFileManager;
import org.flintcore.excel_expenses.serializable.SerializeReader;
import org.flintcore.excel_expenses.serializable.SerializeWriter;
import org.flintcore.excel_expenses.models.receipts.Receipt;

public abstract class ReceiptSerializeFileManager<T extends Receipt> extends SerializeFileManager<T> {
    protected static final String[] RECEIPTS_PATH = {"app", "storage", "receipts"};

    public ReceiptSerializeFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }
}
