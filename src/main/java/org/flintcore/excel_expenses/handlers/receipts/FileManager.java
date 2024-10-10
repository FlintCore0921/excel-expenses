package org.flintcore.excel_expenses.handlers.receipts;

import org.flintcore.excel_expenses.handlers.files.SerializeReader;
import org.flintcore.excel_expenses.handlers.files.SerializeWriter;
import org.flintcore.excel_expenses.models.expenses.Receipt;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class FileManager {
    protected final ReentrantReadWriteLock lock;
    protected final Lock writeLock;
    protected final Lock readLock;

    // Injected
    protected final SerializeWriter serializeWriter;
    protected final SerializeReader serializeReader;

    protected final String[] FILE_PATH;

    public FileManager(
            String[] filePath,
            SerializeWriter serializeWriter,
            SerializeReader serializeReader) {
        this.serializeWriter = serializeWriter;
        this.serializeReader = serializeReader;
        this.FILE_PATH = filePath;

        this.lock = new ReentrantReadWriteLock(true);
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public <R extends Serializable, L extends Collection<R> & Serializable> void updateReceiptsData(L receipts) {
        writeLock.lock();
        try {
            this.serializeWriter.writeIn(FILE_PATH, receipts);
        } finally {
            writeLock.unlock();
        }
    }

    public Set<Receipt> getReceipts() {
        readLock.lock();
        HashSet<Receipt> receipts = new HashSet<>();
        try {
            Collection<Receipt> read = this.serializeReader.read(FILE_PATH);
            receipts.addAll(read);
        } finally {
            readLock.unlock();
        }
        return receipts;
    }
}
