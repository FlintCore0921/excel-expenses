package org.flintcore.excel_expenses.serializable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.paths.FilePathHolder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
@Scope("prototype")
@Log4j2
@RequiredArgsConstructor
public class SerializeWriter {

    private final SerializableFileCreator serializableFileCreator;
    private final SerializableFileFinder serializableFileFinder;

    public <T extends Serializable> void writeIn(@NonNull FilePathHolder pathHolder, T data) {
        AtomicReference<File> fileResource = new AtomicReference<>();

        String[] fullPath = pathHolder.fullPath();
        serializableFileFinder.getSerializeFile(fullPath)
                .ifPresentOrElse(
                        fileResource::set,
                        () -> createNewFileFrom(fullPath, fileResource::set)
                );

        if (fileResource.get() == null) {
            String path = Arrays.toString(pathHolder.fullPath());
            log.warn("Unable to read file in path: {}", path);
            return;
        }

        try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(fileResource.get()))) {
            objStream.writeObject(data);
        } catch (IOException ignored) {
        }
    }

    private void createNewFileFrom(@NonNull String[] fileName, Consumer<File> set) {
        try {
            File serializeFile = this.serializableFileCreator
                    .createSerializeFile(fileName);

            set.accept(serializeFile);
        } catch (FileAlreadyExistsException ignored) {
        }
    }
}
