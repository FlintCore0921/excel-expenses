package org.flintcore.excel_expenses.services.receipts;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.holders.ExpenseItemManager;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;

@Log4j2
@Component
public final class MainExpenseFileItemService {
    private final ExpenseItemManager expenseItemManager;
    private final ReceiptFileScheduledFXService expenseFileService;

    private volatile boolean hasAttached;

    public MainExpenseFileItemService(
            ExpenseItemManager expenseItemManager,
            ReceiptFileScheduledFXService expenseFileService
    ) {
        this.expenseItemManager = expenseItemManager;
        this.expenseFileService = expenseFileService;
    }

    public void requestData() {
        if (this.hasAttached) return;

        this.hasAttached = true;

        log.info("Requesting main expense file items");
        this.expenseFileService.getDataList().thenAcceptAsync(
                this.expenseItemManager::setItems, Platform::runLater
        );
    }

    public void applyFilter(Predicate<Receipt> filterPredicate) {
        this.expenseItemManager.setFilter(filterPredicate);
    }

    public CompletableFuture<ObservableList<Receipt>> getFilterDataList() {
        return CompletableFuture.supplyAsync(this.expenseItemManager::getReceipts);
    }

    // Detach the list and try to reassign with more recent values.
    public void resetData() {
        if (!this.hasAttached) return;

        this.hasAttached = false;

        Platform.runLater(
                () -> this.expenseItemManager.setItems(FXCollections.emptyObservableList())
        );

        log.info("Resetting data inside the main expense file items");
        PauseTransition delayTimer = new PauseTransition(Duration.ONE);

        delayTimer.setOnFinished(evt -> this.requestData());

        delayTimer.play();
    }
}
