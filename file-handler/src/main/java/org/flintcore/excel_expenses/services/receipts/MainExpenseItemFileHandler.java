package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import javafx.util.Subscription;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.transitions.TransitionFactory;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.managers.filters.ExpenseMatchFilter;
import org.flintcore.excel_expenses.managers.holders.ExpenseItemViewManager;
import org.flintcore.excel_expenses.models.observables.collections.OnChangeListener;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
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
    private FilteredList<Receipt> filteredReceipt;
    private ObservableList<? extends Receipt> expenseListSource;
    private boolean hasAttached;

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
                    expenseListSource = receipts;
                    setFilterListeners();
                    registerViews(receipts);
                }, Platform::runLater)
                .exceptionallyAsync(th -> {
                    System.out.println(th.getMessage());
                    return null;
                });

        log.info("Data requested!");
    }

    public Subscription listenReceiptsViews(OnChangeListener<IItemViewHandler<Receipt, Node>> changes) {
        MapChangeListener<? super IItemViewHandler<Receipt, Node>, ? super Transition> changeListener = chMap -> {
            if (chMap.wasAdded()) changes.onAdded(chMap.getKey());
            if (chMap.wasRemoved()) changes.onRemove(chMap.getKey());
        };
        this.expenseItemViewManager.getExpenseHandlers().addListener(changeListener);

        return () -> this.expenseItemViewManager.getExpenseHandlers().removeListener(changeListener);
    }

    public void setParentPane(Supplier<@NonNull Pane> parentPane) {
        this.parentPane = parentPane;

        if (Objects.nonNull(parentPane)) {
            ObservableList<Node> parentChildrenList = this.parentPane.get().getChildren();
            this.expenseItemViewManager.getExpenseHandlers().keySet().forEach(
                    handler -> parentChildrenList.add(handler.getView())
            );
        }
    }

    public void setNodeTransitions(BiFunction<TransitionFactory, Node, Transition> transitionBuilder) {
        this.expenseItemViewManager.setTransitionBuilder(node -> {
            if (Objects.isNull(parentPane)) {
                log.warn("Requires a parent pane to set views.");
                return null;
            }
            final Pane parentPaneRef = this.parentPane.get();

            node.setOpacity(0);

            // If parent does not contains value, added it.
            if (!parentPaneRef.getChildren().contains(node)) {
                parentPaneRef.getChildren().add(node);
            }

            return transitionBuilder.apply(this.transitionFactory, node);
        });
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
                this.expenseItemViewManager.cleanFilter();
                return;
            }

            Predicate<Receipt> receiptPredicate = rep -> this.matchFilter.filterByContains(text,
                    rep::NFC,
                    rep.getTotalPrice()::toString,
                    rep.business()::getName,
                    rep.business()::getRNC
            );

            this.expenseItemViewManager.setReceiptFilter(receiptPredicate);
        });

        // Listen service changes
        expenseListSource.addListener((ListChangeListener<? super Receipt>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Receipt receipt : c.getAddedSubList()) {
                        this.expenseItemViewManager.createNewHandler(receipt);
                        this.expenseItemViewManager.getHandler(receipt).ifPresent(handler -> {
                            IItemViewHandler<Receipt, Node> viewHandler = handler.getKey();
                            viewHandler.setOnEdit(rep -> {/* TODO */});

                            viewHandler.setOnRemove(this.expenseFileService::delete);
                        });

                        if (Objects.nonNull(this.parentPane)) {
                            triggerShow(receipt);
                        }
                    }
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(this::triggerHide);
                }
            }
        });

        // Listen to item handlerFiltering
        this.listenReceiptsViews(new OnChangeListener<>() {
            @Override
            public void onAdded(IItemViewHandler<Receipt, Node> value) {
                triggerShow(value.getValue());
            }

            @Override
            public void onRemove(IItemViewHandler<Receipt, Node> value) {
                triggerHide(value.getValue());
            }
        });

        log.info("Set filter source listener");
    }

    private void triggerShow(Receipt receipt) {
        this.expenseItemViewManager.getHandler(receipt).ifPresent(
                pair -> handleAnimationOn(pair, 1)
        );
    }

    private void triggerHide(Receipt receipt) {
        this.expenseItemViewManager.removeHandler(receipt)
                .ifPresent(entry -> handleAnimationOn(entry, -1));
    }

    private void handleAnimationOn(Pair<IItemViewHandler<Receipt, Node>, Transition> handlerEntry, int interval) {
        IItemViewHandler<Receipt, Node> viewHandler = handlerEntry.getKey();
        Transition transition = handlerEntry.getValue();
        transition.stop();

        transition.setOnFinished(e -> {
            if (interval < 0) {
                NullableUtils.executeNonNull(this.parentPane,
                        paneSupplier -> paneSupplier.get().getChildren()
                                .remove(viewHandler.getView())
                );
            }
        });

        transition.setRate(interval);
        transition.playFromStart();
    }
}
