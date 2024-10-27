package org.flintcore.excel_expenses.models.alerts;

import data.utils.NullableUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.models.events.TaskFxEvent;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableTask;

import java.util.function.Consumer;

public class ObservableTaskAlert<T> extends Alert {
    public ObservableTask<T> linkedTask;
    private Subscription onCompletedSubs;

    public ObservableTaskAlert(ObservableTask<T> linkedTask) {
        super(AlertType.CONFIRMATION);
        this.linkedTask = linkedTask;
        configure();
        setupTaskListeners();
        setupCancelButtons();
    }

    private void configure() {
        initModality(Modality.NONE);

    }

    private void setupTaskListeners() {
        this.linkedTask.addSubscription(
                TaskFxEvent.WORKER_STATE_SCHEDULED, this::show
        );
    }

    private void setupCancelButtons() {
        ButtonType cancelButton = ButtonType.CANCEL;
        getButtonTypes().setAll(cancelButton);

        getDialogPane().lookupButton(cancelButton)
                .setOnMouseClicked(__ -> this.close());
    }

    public void setOnCompleted(Consumer<Alert> onCompleted) {
        NullableUtils.executeNonNull(this.onCompletedSubs, Subscription::unsubscribe);

        onCompletedSubs = this.linkedTask.addSubscription(TaskFxEvent.WORKER_STATE_SUCCEEDED,
                () -> onCompleted.accept(this)
        );
    }
}
