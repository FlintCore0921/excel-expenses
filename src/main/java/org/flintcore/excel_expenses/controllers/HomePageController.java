package org.flintcore.excel_expenses.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.routers.expenses.EExpenseRoute;
import org.flintcore.excel_expenses.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.local.ELocalRoute;
import org.flintcore.excel_expenses.services.receipts.MainExpenseFileItemService;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
@Log4j2
public class HomePageController implements Initializable {

    private final ApplicationRouter appRouter;
    private final MainExpenseFileItemService expenseFileListManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerRouting();

        // Start requesting data from service
        settingReceiptItems();
    }

    private void registerRouting() {
        // Expense form
        this.btnNewReceipt.setOnAction(evt -> this.appRouter.navigateTo(EExpenseRoute.CREATE));
        // Local Form
        this.btnNewBusiness.setOnAction(evt -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }

    private void settingReceiptItems() {

        log.info("Start requesting data from service.");
        PauseTransition delayRequest = new PauseTransition(Duration.seconds(5));

        delayRequest.setOnFinished(evt -> this.expenseFileListManager.requestData());

        delayRequest.play();

        // Request and assign data to
    }

    @FXML
    private Button btnNewBusiness;

    @FXML
    private Button btnNewReceipt;

    @FXML
    private VBox expenseHolder;
}
