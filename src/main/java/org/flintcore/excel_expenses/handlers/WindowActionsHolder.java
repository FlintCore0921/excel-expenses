package org.flintcore.excel_expenses.handlers;

import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public record WindowActionsHolder(Circle btnClose, Circle btnMinimize) {
    public WindowActionsHolder(Circle btnClose, Circle btnMinimize) {
        this.btnClose = btnClose;
        this.btnMinimize = btnMinimize;

        initialize();
    }

    public void initialize() {
        btnClose.setOnMouseClicked(event -> Platform.exit());
        btnMinimize.setOnMouseClicked($ -> {
            Stage stage = (Stage) btnMinimize.getScene().getWindow();
            stage.setIconified(true);
        });
    }
}
