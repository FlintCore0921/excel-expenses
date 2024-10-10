package org.flintcore.excel_expenses.controllers;

import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
public class ExpensePageController implements Initializable {

    @FXML
    private Button btnCreateExpense;

    @FXML
    private VBox receiptListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

}
