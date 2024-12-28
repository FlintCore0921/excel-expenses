package org.flintcore.excel_expenses.services.connections;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.concurrent.FutureUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Connector class to validate access to EXE file.
 */
@Log4j2
public abstract class ExeFileConnection implements IServerConnection {

    protected static final long WAIT_TIME_REQUEST = 5L;
    protected static final int WAIT_ATTEMPTS = 5;

    protected final String FILE_PATH;
    protected final AtomicReference<State> connectorState;
    /**
     * Exe File process communicator.
     */
    protected Process fileProcess;
    protected volatile boolean isClosed;

    protected static final class Holder {
        /**
         * Thread Pool executor with some threads.
         */
        private static final ExecutorService EXECUTOR_SERVICE =
                Executors.newFixedThreadPool(3);
    }

    public ExeFileConnection(String file_path) {
        this.FILE_PATH = file_path;
        this.isClosed = false;
        this.connectorState = new AtomicReference<>(State.DOWN);
    }

    @Override
    public void init() {
        if (!isClosed() && !this.connectorState.compareAndSet(State.DOWN, State.RESETTING))
            return;

        log.info("Resetting service...");

        Holder.EXECUTOR_SERVICE.submit(() -> {
            this.connectorState.set(State.RUNNING);

            try {
                // Checks
                checksFileExists();
                fileProcess = new ProcessBuilder(FILE_PATH).start();
                validateProcessState();
            } catch (IOException e) {
                log.error("Error opening file", e);
                this.close();
            }

            this.connectorState.set(State.CONNECTED);

            return null;
        });
    }

    private void checksFileExists() throws FileNotFoundException {
        final Path path = Path.of(FILE_PATH);

        log.info("Opening exe file in {}", path.toAbsolutePath());

        if (Files.notExists(path))
            throw new FileNotFoundException();
    }

    @Override
    public boolean isAlive() {
        return !this.isClosed() && !this.connectorState.get().equals(State.DOWN);
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public State getState() {
        return this.connectorState.get();
    }

    @Override
    public Future<State> waitUntil(@NonNull State state) {
        if (this.connectorState.get().equals(state))
            return CompletableFuture.completedFuture(state);

        this.init();

        return FutureUtils.callAsync(() -> {
            State resultState = this.connectorState.get();

            if (resultState.equals(state)) return resultState;

            int tries = WAIT_ATTEMPTS;

            do {
                // Wait a moment to continue.
                TimeUnit.SECONDS.sleep(WAIT_TIME_REQUEST);
                resultState = this.connectorState.get();
                // If tries fails, throws timeout exception.
                // TODO Create a simple exception.
                if (--tries == 0) throw new TimeoutException("Server response time out.");
            } while (!state.equals(resultState));

            return resultState;
        }, Holder.EXECUTOR_SERVICE);
    }

    protected void validateProcessState() throws IOException {
        if (fileProcess.isAlive()) return;

        this.close();
        this.fileProcess = null;
    }

    @Override
    public void close() throws IOException {
        this.connectorState.set(State.DOWN);
        this.fileProcess.destroy();
        this.isClosed = true;
    }
}
