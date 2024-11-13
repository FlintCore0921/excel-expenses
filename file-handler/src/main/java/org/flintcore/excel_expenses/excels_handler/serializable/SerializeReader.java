package org.flintcore.excel_expenses.excels_handler.serializable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.excels_handler.files.paths.FilePathHolder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
@Scope("prototype")
@RequiredArgsConstructor
public class SerializeReader {

    private final SerializableFileFinder serializableFileFinder;

    public <T extends Serializable> T read(@NonNull FilePathHolder filePath) {
        AtomicReference<T> resultData = new AtomicReference<>();

        Optional<File> serializeFile = serializableFileFinder.getSerializeFile(filePath);

        serializeFile.ifPresent(readFileConsumer(resultData));

        return resultData.get();
    }

    @SuppressWarnings("unchecked")
    private <T extends Serializable> Consumer<File> readFileConsumer(AtomicReference<T> resultData) {
        return file -> {
            try (ObjectInputStream objStream = new ObjectInputStream(new FileInputStream(file))) {
                resultData.set((T) objStream.readObject());
            } catch (IOException | ClassNotFoundException ignored) {
            }
        };
    }
}
