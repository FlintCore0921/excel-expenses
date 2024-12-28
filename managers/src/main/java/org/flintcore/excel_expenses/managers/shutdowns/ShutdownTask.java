package org.flintcore.excel_expenses.managers.shutdowns;

import javafx.concurrent.Task;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ShutdownTask extends Task<Void> {
    private final Runnable task;

    public static ShutdownTask create(Runnable task) {
        return new ShutdownTask(task);
    }

    @Override
    protected Void call() throws Exception {
        task.run();
        return null;
    }
}
