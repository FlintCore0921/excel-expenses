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
public class ReceiptFileManager {

    public static final String[] RECEIPT_FILE_PATH = {"app", "storage", "data", "receipts.txt"};
    private final ReentrantReadWriteLock lock;
    private final Lock writeLock;
    private final Lock readLock;

    // Injected
    private final SerializeWriter serializeWriter;
    private final SerializeReader serializeReader;

    public ReceiptFileManager(SerializeWriter serializeWriter, SerializeReader serializeReader) {
        this.serializeWriter = serializeWriter;
        this.serializeReader = serializeReader;
        lock = new ReentrantReadWriteLock(true);
        writeLock = lock.writeLock();
        readLock = lock.readLock();
    }

    public <R extends Serializable, L extends Collection<R> & Serializable> void updateReceiptsData(L receipts) {
        writeLock.lock();
        try {
            this.serializeWriter.writeIn(RECEIPT_FILE_PATH, receipts);
        } finally {
            writeLock.unlock();
        }
    }

    public Set<Receipt> getReceipts() {
        readLock.lock();
        HashSet<Receipt> receipts = new HashSet<>();
        try {
            Collection<Receipt> read = this.serializeReader.read(RECEIPT_FILE_PATH);
            receipts.addAll(read);
        } finally {
            readLock.unlock();
        }
        return receipts;
    }
}
