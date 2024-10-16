package org.flintcore.excel_expenses.files;

import data.utils.NullableUtils;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class FileManager<T extends Serializable> {
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

    public void updateDataSet(SerialListHolder<T> data) {
        try {
            writeLock.lock();
            this.serializeWriter.writeIn(FILE_PATH, new ArrayList<>(data.values()));
        } finally {
            writeLock.unlock();
        }
    }

    public List<T> getDataList() {
        ArrayList<T> dataList = new ArrayList<>();
        Collection<T> read;

        try {
            readLock.lock();
            read = this.serializeReader.read(FILE_PATH);
        } finally {
            readLock.unlock();
        }
        NullableUtils.executeIsNull(read, () -> updateDataSet(new SerialListHolder<>(dataList)));
        NullableUtils.executeNonNull(read, dataList::addAll);

        return dataList;
    }
}
