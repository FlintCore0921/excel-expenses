package org.flintcore.excel_expenses.files;

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
public class SerializeWriter {

    private final SerializableFileCreator serializableFileCreator;
    private final SerializableFileFinder serializableFileFinder;

    public <T extends Serializable> void writeIn(@NonNull String[] fileName, T data) {
        AtomicReference<File> fileResource = new AtomicReference<>();

        serializableFileFinder.getSerializeFile(fileName)
                .ifPresentOrElse(
                        fileResource::set,
                        () -> createNewFileFrom(fileName, fileResource::set)
                );

        if(fileResource.get() == null) {
            return;
        }

        try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(fileResource.get()))) {
            objStream.writeObject(data);
        } catch (IOException ignored) {}
    }

    private void createNewFileFrom(@NonNull String[] fileName, Consumer<File> set) {
        try {
            File serializeFile = this.serializableFileCreator
                    .createSerializeFile(fileName);

            set.accept(serializeFile);
        } catch (FileAlreadyExistsException e) {}
    }
}
