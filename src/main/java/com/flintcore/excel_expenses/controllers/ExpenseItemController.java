package com.flintcore.excel_expenses.controllers;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Component
public class ExpenseItemController implements Initializable {

    @FXML
    private Label dateTxt;

    @FXML
    private Label localNameTxt;

    @FXML
    private Label priceTxt;

    @FXML
    private HBox optionsBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
