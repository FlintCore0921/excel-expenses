package org.flintcore.utilities.runnables;

@FunctionalInterface
public interface RunnableThrowable {
    void run() throws Exception;
}
