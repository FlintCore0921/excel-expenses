package org.flintcore.excel_expenses.excels_handler.managers.alerts;

import javafx.scene.control.Alert;
import lombok.NonNull;

import java.util.function.Consumer;

public final class AlertFXBuilder {
    private AlertFXBuilder() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Alert buildAlertWith(
            Alert.AlertType type, @NonNull Consumer<Alert> settings
    ) {
        Alert alert = new Alert(type);
        settings.accept(alert);
        return alert;
    }
}
