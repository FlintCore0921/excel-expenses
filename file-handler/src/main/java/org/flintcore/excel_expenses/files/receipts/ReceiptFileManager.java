package org.flintcore.excel_expenses.files.receipts;

import org.flintcore.excel_expenses.files.FileManager;
import org.flintcore.excel_expenses.files.SerializeReader;
import org.flintcore.excel_expenses.files.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.Receipt;
import org.springframework.stereotype.Component;

@Component
public class ReceiptFileManager extends FileManager<Receipt> {
    public static final String[] RECEIPT_FILE_PATH = {"app", "storage", "data", "receipts.txt"};

    public ReceiptFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(RECEIPT_FILE_PATH, serializeWriter, serializeReader);
    }
}
