package com.flintcore.excel_expenses.controllers;

import com.flintcore.excel_expenses.models.expenses.IBusiness;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

@Component
public class ExpenseCreateFormController {

    @FXML
    private Button btnRegisterLocal;

    @FXML
    private Button btnSubmit;

    @FXML
    private ComboBox<IBusiness> localFilterBox;

    @FXML
    private TextField localNameTxt;

    @FXML
    private TextField localRNCTxt;

    @FXML
    private TextField percentServicePriceTxt;

    @FXML
    private HBox priceExtraHolder;

    @FXML
    private VBox priceHolder;

    @FXML
    private TextField priceItbisTxt;

    @FXML
    private TextField priceTxt;

    @FXML
    private DatePicker receiptDate;

    @FXML
    private TextField receiptNFCTxt;

}
