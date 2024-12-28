package org.flintcore.excel_expenses.controllers.expenses.register;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.FXVolatileController;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;

import java.net.URL;
import java.util.ResourceBundle;

@FXVolatileController
@Log4j2
@RequiredArgsConstructor
public class ExpenseRegisterReceiptController implements Initializable {

    private final CompoundResourceBundle resources;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private DatePicker receiptDate;

    @FXML
    private TextField rncField;

}
