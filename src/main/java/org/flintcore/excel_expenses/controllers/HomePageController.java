package org.flintcore.excel_expenses.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.routers.expenses.EExpenseRoute;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.local.ELocalRoute;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class HomePageController implements Initializable {

    private final ApplicationRouter appRouter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnNewReceipt.setOnAction(evt -> this.appRouter.navigateTo(EExpenseRoute.CREATE));
        this.btnNewBusiness.setOnAction(evt -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }

    @FXML
    private Button btnNewBusiness;

    @FXML
    private Button btnNewReceipt;

    @FXML
    private VBox expenseHolder;
}
