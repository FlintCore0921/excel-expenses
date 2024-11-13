package org.flintcore.excel_expenses.excels_handler.serializable;

import data.utils.NullableUtils;
import org.flintcore.excel_expenses.excels_handler.files.extensions.ESerializableExtension;
import org.flintcore.excel_expenses.excels_handler.files.paths.FilePathHolder;
import org.flintcore.excel_expenses.excels_handler.models.lists.SerialListHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class SerializeFileManager<T extends Serializable> {
    protected final ESerializableExtension DEFAULT_SERIAL_EXTENSION =
            ESerializableExtension.getDefault();

    protected final ReentrantReadWriteLock lock;
    protected final Lock writeLock;
    protected final Lock readLock;

    // Injected
    protected final SerializeWriter serializeWriter;
    protected final SerializeReader serializeReader;

    public SerializeFileManager(
            SerializeWriter serializeWriter,
            SerializeReader serializeReader) {
        this.serializeWriter = serializeWriter;
        this.serializeReader = serializeReader;

        this.lock = new ReentrantReadWriteLock(true);
        this.writeLock = lock.writeLock();
        this.readLock = lock.readLock();
    }

    public void updateDataSet(SerialListHolder<T> data) {
        try {
            writeLock.lock();
            this.serializeWriter.writeIn(getFilePath(), new ArrayList<>(data.values()));
        } finally {
            writeLock.unlock();
        }
    }

    public List<T> getDataList() {
        ArrayList<T> dataList = new ArrayList<>();
        Collection<T> read;

        try {
            readLock.lock();
            read = this.serializeReader.read(getFilePath());
        } finally {
            readLock.unlock();
        }
//        NullableUtils.executeIsNull(read, () -> updateDataSet(new SerialListHolder<>(dataList)));
        NullableUtils.executeNonNull(read, dataList::addAll);

        return dataList;
    }

    protected abstract FilePathHolder getFilePath();
}
