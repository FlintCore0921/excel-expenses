package org.flintcore.excel_expenses.excels_handler.controllers;

import jakarta.annotation.PreDestroy;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.excels_handler.managers.routers.expenses.EExpenseRoute;
import org.flintcore.excel_expenses.excels_handler.managers.routers.local.ELocalRoute;
import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.excels_handler.services.receipts.MainExpenseItemFileHandler;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
@Log4j2
public class HomePageController implements Initializable {

    private final ApplicationRouter appRouter;
    private final MainExpenseItemFileHandler expenseFileListManager;
    private final SubscriptionHolder subscriptionHolder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerRouting();

        // Start requesting data from service
        settingExpenseItems();
    }

    private void settingExpenseItems() {
        // Assign the parent view to hold expense items
        this.expenseFileListManager.setParentPane(() -> this.expenseHolder);

        this.subscriptionHolder.appendSubscriptionOn(this,
                () -> this.expenseFileListManager.setParentPane(null)
        );

        setExpenseItemTransitionBuilder();

        // Set textField search
        this.searchField.textProperty().bindBidirectional(
                this.expenseFileListManager.getFilterText()
        );

        log.info("Start requesting data from service.");
        // Pause delay
        PauseTransition delayRequest = new PauseTransition(Duration.seconds(5));

        delayRequest.setOnFinished(evt -> this.expenseFileListManager.loadData());

        delayRequest.playFromStart();
    }

    private void setExpenseItemTransitionBuilder() {
        this.expenseFileListManager.setNodeTransitions((transitionFactory, node) -> transitionFactory.createSequentialTransition(
                node, Duration.millis(MainExpenseItemFileHandler.ANIMATION_DURATION),
                List.of(transitionFactory::createFadeInTransition,
                        transitionFactory::createSlideInHorizontallyTransition
                )
        ));
    }

    private void registerRouting() {
        // Expense form
        this.btnNewReceipt.setOnAction(evt -> this.appRouter.navigateTo(EExpenseRoute.CREATE));
        // Local Form
        this.btnNewBusiness.setOnAction(evt -> this.appRouter.navigateTo(ELocalRoute.CREATE));
    }


    @PreDestroy
    public void onClose() {
        Platform.runLater(this.subscriptionHolder::close);
    }

    @FXML
    private Button btnNewBusiness;

    @FXML
    private Button btnNewReceipt;

    @FXML
    private VBox expenseHolder;

    @FXML
    private TextField searchField;
}
