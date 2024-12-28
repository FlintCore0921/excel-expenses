package org.flintcore.utilities.runnables;

import lombok.SneakyThrows;

@FunctionalInterface
public interface RunnableThrowableOmitted extends Runnable {
    default void run() {
        try {
            onHandle();
        } catch (Exception ignored) {
        }
    }

    void onHandle() throws Exception;

    @SneakyThrows
    static Runnable asRunnable(RunnableThrowableOmitted r) {
        return r;
    }
}
