package org.flintcore.excel_expenses.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.flintcore.excel_expenses.managers.routers.expenses.EExpenseRoute;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;


@Component
public class ExpensePageController implements Initializable {

    private final ApplicationRouter appRouter;

    public ExpensePageController(ApplicationRouter appRouter) {
        this.appRouter = appRouter;
    }

    @FXML
    private Button btnCreateExpense;

    @FXML
    private VBox receiptListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.btnCreateExpense.setOnAction(evt -> this.appRouter.navigateTo(EExpenseRoute.CREATE));
    }

}
