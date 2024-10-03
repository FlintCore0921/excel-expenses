package com.flintcore.excel_expenses.handlers.files;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SerializeReader {

    private final SerializableFileCreator serializableFileCreator;
    private final SerializableFileFinder serializableFileFinder;

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T read(@NonNull String[] fileName) {
        AtomicReference<T> resultData = new AtomicReference<>();

        serializableFileFinder.getSerializeFile(fileName).ifPresent(file -> {
            try (ObjectInputStream objStream = new ObjectInputStream(new FileInputStream(file))) {
                resultData.set((T) objStream.readObject());
            } catch (IOException ignored) {
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return resultData.get();
    }

    private void createNewFileFrom(@NonNull String[] fileName, Consumer<File> set) {
        try {
            File serializeFile = this.serializableFileCreator
                    .createSerializeFile(fileName);

            set.accept(serializeFile);
        } catch (FileAlreadyExistsException ignored) {}
    }
}
