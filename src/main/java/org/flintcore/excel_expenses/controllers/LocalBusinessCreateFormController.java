package org.flintcore.excel_expenses.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LocalBusinessCreateFormController implements Initializable {
    private final ApplicationRouter appRouter;

    public LocalBusinessCreateFormController(ApplicationRouter appRouter) {
        this.appRouter = appRouter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnBack.setOnMousePressed(evt -> this.appRouter.navigateBack());
    }

    @FXML
    private Button btnBack;
    @FXML
    private Button btnSave;

    @FXML
    private TextField localRNCTxt;

    @FXML
    private TextField localNameTxt;

    @FXML
    private BorderPane titleContainer;
}
