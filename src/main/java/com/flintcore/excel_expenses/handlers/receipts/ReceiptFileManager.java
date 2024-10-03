package com.flintcore.excel_expenses.handlers.receipts;

import com.flintcore.excel_expenses.handlers.files.SerializeReader;
import com.flintcore.excel_expenses.handlers.files.SerializeWriter;
import com.flintcore.excel_expenses.models.expenses.Receipt;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class ReceiptFileManager extends FileManager {
    public static final String[] RECEIPT_FILE_PATH = {"app", "storage", "data", "receipts.txt"};

    public ReceiptFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        super(RECEIPT_FILE_PATH, serializeWriter, serializeReader);
    }
}
