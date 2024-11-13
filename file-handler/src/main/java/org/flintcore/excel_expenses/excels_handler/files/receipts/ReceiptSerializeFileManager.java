package org.flintcore.excel_expenses.excels_handler.files.receipts;

import org.flintcore.excel_expenses.excels_handler.serializable.SerializeFileManager;
import org.flintcore.excel_expenses.excels_handler.serializable.SerializeReader;
import org.flintcore.excel_expenses.excels_handler.serializable.SerializeWriter;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;

public abstract class ReceiptSerializeFileManager<T extends Receipt> extends SerializeFileManager<T> {
    protected static final String[] RECEIPTS_PATH = {"app", "storage", "receipts"};

    public ReceiptSerializeFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(serializeWriter, serializeReader);
    }
}
