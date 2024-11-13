package org.flintcore.excel_expenses.excels_handler.models.alerts;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.function.Consumer;

@Log4j2
public class ObservableTaskAlert extends Alert {
    private static final Duration CLOSE_DELAY = Duration.seconds(12);

    private final Subscription onCancel;

    private Property<Worker.State> state;
    @Setter
    private Consumer<Alert> onCompleted;

    public ObservableTaskAlert(Subscription onCancel) {
        super(AlertType.CONFIRMATION);
        this.onCancel = onCancel;
        configure();
        setupCancelButtons();
    }

    private void configure() {
        initModality(Modality.NONE);
    }

    public void bindMessage(ObservableValue<String> messageProp) {
        this.contentTextProperty().unbind();
        this.contentTextProperty().bind(messageProp);
    }

    public void bindTaskState(ObservableValue<Worker.State> stateProp) {
        NullableUtils.executeIsNull(this.state, () -> {
            this.state = new SimpleObjectProperty<>();
            setupTaskListeners();
        });

        this.state.unbind();
        this.state.bind(stateProp);
    }

    private void setupTaskListeners() {
        this.state.subscribe((old, status) -> {
            switch (status) {
                case SCHEDULED -> this.show();
                case SUCCEEDED -> {
                    setupCompleteButtons();

                    NullableUtils.executeNonNull(this.onCompleted,
                            () -> onCompleted.accept(this)
                    );
                }
                case CANCELLED -> {
                    onCancel.unsubscribe();
                    this.hide();
                }
                case FAILED -> this.hide();
            }
        });
    }

    private void setupCompleteButtons() {
        getButtonTypes().setAll(ButtonType.OK);

        PauseTransition onTimeout = new PauseTransition(CLOSE_DELAY);
        onTimeout.setOnFinished(__ -> this.close());

        onTimeout.play();
    }

    private void setupCancelButtons() {
        ButtonType cancelButton = ButtonType.CANCEL;
        getButtonTypes().setAll(cancelButton);

        getDialogPane().lookupButton(cancelButton)
                .setOnMouseClicked(__ -> this.close());
    }
}
