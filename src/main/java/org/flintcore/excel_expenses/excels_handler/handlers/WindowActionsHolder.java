package org.flintcore.excel_expenses.excels_handler.handlers;

import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

public final class WindowActionsHolder {
    private final Circle btnClose;
    private final Circle btnMinimize;

    private Closeable shutdownHandler;


    public WindowActionsHolder(Circle btnClose, Circle btnMinimize) {
        this.btnClose = btnClose;
        this.btnMinimize = btnMinimize;

        initialize();
    }

    public void initialize() {
        btnClose.setOnMouseClicked(event -> {
            try {
                shutdownHandler.close();
            } catch (IOException e) {
                Platform.exit();
            }
        });
        btnMinimize.setOnMouseClicked($ -> {
            Stage stage = (Stage) btnMinimize.getScene().getWindow();
            stage.setIconified(true);
        });
    }

    public void setShutdown(Closeable shutdownHolder) {
        shutdownHandler = shutdownHolder;
    }

    public Circle btnClose() {
        return btnClose;
    }

    public Circle btnMinimize() {
        return btnMinimize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WindowActionsHolder) obj;
        return Objects.equals(this.btnClose, that.btnClose) &&
                Objects.equals(this.btnMinimize, that.btnMinimize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(btnClose, btnMinimize);
    }

    @Override
    public String toString() {
        return "WindowActionsHolder[" +
                "btnClose=" + btnClose + ", " +
                "btnMinimize=" + btnMinimize + ']';
    }

}
