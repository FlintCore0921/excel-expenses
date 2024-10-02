package com.flintcore.excel_expenses.controllers;

import com.flintcore.excel_expenses.handlers.WindowActionsHolder;
import com.flintcore.excel_expenses.handlers.routers.EMainRoute;
import com.flintcore.excel_expenses.handlers.routers.MainViewRouter;
import com.flintcore.excel_expenses.listeners.WindowRelocationHandler;
import com.flintcore.excel_expenses.models.RelocationParam;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class MainViewController implements Initializable {

    @FXML
    private Label topbarTitle;

    @FXML
    private Circle btnMinimize;

    @FXML
    private Circle btnClose;

    @FXML
    private VBox navbarActions;

    @FXML
    private ScrollPane bodyPane;

    private final MainViewRouter router;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new WindowActionsHolder(btnClose, btnMinimize);
        this.setHomePane();
    }

    public void setHomePane() {
        try {
            this.router.navigateTo(EMainRoute.Home, this.bodyPane::setContent);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Unable to start application");
            alert.showAndWait();

            Platform.exit();
        }
    }
}
