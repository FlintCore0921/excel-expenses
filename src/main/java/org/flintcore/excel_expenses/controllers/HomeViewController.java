package org.flintcore.excel_expenses.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.IApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Log4j2
@RequiredArgsConstructor
public class HomeViewController implements Initializable {
    private final IApplicationRouter<EMainRoute> mainRouter;
    private final CompoundResourceBundle appBundle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Init values

        // Handle navigation

        // Handle view clicks and resource.
    }

    @FXML
    private Button btnCreateExpense;

    @FXML
    private Button btnExportExpense;

    @FXML
    private Label lbAmount;

    @FXML
    private Label lbAmountTitle;
}
