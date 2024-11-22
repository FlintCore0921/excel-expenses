package org.flintcore.excel_expenses.serializable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.paths.FilePathHolder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
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
        log.info("Getting data file...");
        serializableFileFinder.getSerializeFile(pathHolder)
                .ifPresentOrElse(fileResource::set,
                        () -> requestNewFileFrom(pathHolder, fileResource::set)
                );

        if (fileResource.get() == null) {
            String path = pathHolder.asFullStringPath();
            log.warn("Unable to read file in path: {}", path);
            return;
        }

        log.info("Saving data on file...");
        try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(fileResource.get()))) {
            objStream.writeObject(data);
        } catch (IOException ignored) {
            log.info("Failed saving data...");
        }

    }

    private void requestNewFileFrom(@NonNull FilePathHolder fileName, Consumer<File> setter) {
        try {
            this.serializableFileCreator
                    .createSerializeFile(fileName)
                    .ifPresent(setter);
        } catch (FileAlreadyExistsException ignored) {
        }
    }
}
