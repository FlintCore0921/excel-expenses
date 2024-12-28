package org.flintcore.excel_expenses.services.connections;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

public interface IServerConnection extends Closeable {
    enum State {
        RUNNING, CONNECTED, DOWN, RESETTING;
    }

    void init() throws IOException;

    boolean isAlive();

    boolean isClosed();

    State getState();

    Future<State> waitUntil(State state);
}
