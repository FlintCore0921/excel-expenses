package org.flintcore.excel_expenses.models.alerts;

import events.TaskFxEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableTask;

public class CancelableTaskAlert extends Alert {
    public ObservableTask<?> linkedTask;

    public CancelableTaskAlert(ObservableTask<?> linkedTask) {
        super(AlertType.CONFIRMATION);
        this.linkedTask = linkedTask;
        setupButtons();
        setOnShown(e -> setupTaskListeners());
    }

    private void setupTaskListeners() {
        this.linkedTask.addSubscription(
                TaskFxEvent.WORKER_STATE_RUNNING, this::show
        );
        this.linkedTask.addSubscription(
                TaskFxEvent.WORKER_STATE_DONE, this::close
        );
    }

    private void setupButtons() {
        ButtonType cancelType = new ButtonType(
                ButtonType.CANCEL.getText(), ButtonBar.ButtonData.CANCEL_CLOSE
        );
        getButtonTypes().add(cancelType);
        Button cancelTaskBtn = (Button) getDialogPane().lookupButton(cancelType);
        cancelTaskBtn.setOnAction(__ -> this.linkedTask.cancel(true));
    }
}
