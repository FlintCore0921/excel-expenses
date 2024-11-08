package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.transitions.TransitionFactory;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.managers.filters.ExpenseMatchFilter;
import org.flintcore.excel_expenses.managers.holders.ExpenseItemViewManager;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Holds the {@link Receipt} into a {@link org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler ItemViewHandler}
 * to animate the visibility of the items.
 */
@Log4j2
@Component
public final class MainExpenseItemFileHandler {
    public static final int ANIMATION_DURATION = 600;

    private final ExpenseItemViewManager expenseItemViewManager;
    private final TransitionFactory transitionFactory;
    //Data services
    private final ReceiptFileScheduledFXService expenseFileService;
    private final ExpenseMatchFilter matchFilter;

    @Getter
    private final StringProperty filterText;

    // Filter the items to animate views.
    private FilteredList<Receipt> filteredExpenseList;
    private boolean hasAttached;

    @Setter
    private Supplier<@NonNull Pane> parentPane;

    public MainExpenseItemFileHandler(
            ExpenseItemViewManager expenseItemViewManager,
            ReceiptFileScheduledFXService expenseFileService,
            ExpenseMatchFilter expenseMatchFilter,
            TransitionFactory transitionFactory
    ) {
        this.expenseItemViewManager = expenseItemViewManager;
        this.expenseFileService = expenseFileService;
        this.matchFilter = expenseMatchFilter;
        this.transitionFactory = transitionFactory;

        this.filterText = new SimpleStringProperty("");
    }

    public void loadData() {
        if (this.hasAttached) return;
        this.hasAttached = true;

        log.info("Requesting main expense file items");
        this.expenseFileService.getDataList().thenAcceptAsync(receipts -> {
                    this.filteredExpenseList = new FilteredList<>(receipts);
                    setFilterListeners();
                    registerViews(receipts);
                }, Platform::runLater)
                .exceptionallyAsync(th -> {
                    System.out.println(th.getMessage());
                    return null;
                });

        log.info("Data requested!");
    }

    public ObservableSet<Node> getItemViews() {
        NullableUtils.executeIsNull(this.itemViews, () -> {
            this.itemViews = FXCollections.observableSet();

            ObservableMap<IItemViewHandler<Receipt, Node>, Transition> expenseHandlers = this.expenseItemViewManager
                    .getExpenseHandlers();

            expenseHandlers.addListener(prepareMapHolderListener());
        });

        return this.itemViews;
    }

    public void setNodeTransitions(BiFunction<TransitionFactory, Node, Transition> transitionBuilder) {
        this.expenseItemViewManager.setTransitionBuilder(node -> {
            if (Objects.isNull(parentPane)) {
                log.warn("Requires a parent pane to set views.");
                return null;
            }

            node.setOpacity(0);
            this.parentPane.get().getChildren().add(node);

            return transitionBuilder.apply(this.transitionFactory, node);
        });
    }

    // Private methods

    private MapChangeListener<? super IItemViewHandler<Receipt, Node>, ? super Transition> prepareMapHolderListener() {
        return change -> {
            IItemViewHandler<Receipt, Node> viewHandler = change.getKey();

            if (change.wasAdded()) {
                this.itemViews.add(viewHandler.getView());
            }

            if (change.wasRemoved()) {
                this.itemViews.remove(viewHandler.getView());
            }
        };
    }

    private void registerViews(List<Receipt> receipts) {
        NullableUtils.executeNonNull(receipts,
                reps -> reps.forEach(this.expenseItemViewManager::createNewHandler)
        );
    }

    private void setFilterListeners() {
        // Set the text to filter data
        this.filterText.subscribe(text -> {
            if (text.isBlank()) {
                this.filteredExpenseList.setPredicate(null);
                return;
            }

            this.filteredExpenseList.setPredicate(rep -> this.matchFilter.filterByContains(text,
                    rep::NFC, rep.getTotalPrice()::toString,
                    rep.business()::getName, rep.business()::getName
            ));
        });

        this.filteredExpenseList.getSource().addListener((ListChangeListener<? super Receipt>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Receipt receipt : c.getAddedSubList()) {
                        this.expenseItemViewManager.createNewHandler(receipt);
                        triggerShow(receipt);
                    }
                }

                if (c.wasRemoved()) {
                    for (Receipt receipt : c.getRemoved()) {
                        // If source list does not contain changed value
                        this.expenseItemViewManager.removeHandler(receipt).ifPresent(
                                transition -> handleAnimationOn(transition, -1)
                        );
                    }
                }
            }

        });
        log.info("Set filter source listener");
    }

    private void triggerShow(Receipt receipt) {
        this.expenseItemViewManager.getHandler(receipt).ifPresent(pair -> {
            Transition viewTransition = pair.getValue();
            handleAnimationOn(viewTransition, 1);
        });
    }

    private void handleAnimationOn(Transition transition, int value) {
        transition.stop();
        transition.setRate(value);
        transition.playFromStart();
    }
}
