package org.flintcore.excel_expenses.services.receipts;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
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
 * Holds the {@link Receipt} into a {@link IItemViewHandler ItemViewHandler}
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
                    this.filteredReceipt = new FilteredList<>(receipts);
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
        } else {
            this.expenseItemViewManager.clear();
        }

    }

    public void setNodeTransitions(
            BiFunction<TransitionFactory, Node, Transition> transitionBuilder
    ) {
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
        final PauseTransition filterDelay = new PauseTransition(Duration.millis(500));

        // Set the text to filter data
        this.filterText.subscribe(text -> {
            filterDelay.stop();
            // Java fx 17 Bug:
            // For JavaFX 17, notable fixes included improvements to ListChangeListener behavior,
            // such as resolving an issue where Change.getRemoved() returned items that were not truly removed
            // under certain conditions. This could influence how added and removed changes are reported in listeners,
            // potentially affecting your list updates with wasAdded
            // and wasRemoved actions (GitHub).

            // Try intermediate steps such as setting the predicate to null
            // temporarily before updating it,
            // which can prevent sequence issues.
            this.filteredReceipt.setPredicate(null);

            if (text.isBlank()) {
                return;
            }

            filterDelay.setOnFinished(e -> {
                Predicate<Receipt> receiptPredicate = rep -> this.matchFilter.filterByContains(text,
                        rep::NFC, rep.getTotalPrice()::toString,
                        rep.business()::getName,
                        rep.business()::getRNC
                );
                this.filteredReceipt.setPredicate(receiptPredicate);
            });

            filterDelay.playFromStart();
        });

        // Listen service changes
        this.filteredReceipt.addListener((ListChangeListener<? super Receipt>) c -> {
            while (c.next()) {
                // Bug order making issues. java 17
                if (c.wasRemoved()) {
                    c.getRemoved().forEach(receipt -> {
                        triggerRemove(receipt);
                        NullableUtils.executeNonNull(this.filteredReceipt.getPredicate(), pred -> {
                            log.info("Log predicate for filter: {}", pred.test(receipt));
                        });
                    });
                }

                if (c.wasAdded()) {
                    for (Receipt receipt : c.getAddedSubList()) {
                        this.expenseItemViewManager.createNewHandler(receipt);

                        this.expenseItemViewManager.getHandler(receipt).ifPresent(handler -> {
                            IItemViewHandler<Receipt, Node> viewHandler = handler.getKey();
                            viewHandler.setOnEdit(rep -> {/* TODO */});

                            viewHandler.setOnRemove(this.expenseFileService::delete);
                        });

                        triggerAdd(receipt);
                    }
                }
            }
        });

    }

    private void triggerAdd(Receipt receipt) {
        this.expenseItemViewManager.getHandler(receipt).ifPresent(
                pair -> handleAnimationOn(pair, 1, e -> {
                    NullableUtils.executeNonNull(this.parentPane, paneSupplier -> {
                        Pane pane = parentPane.get();
                        ObservableList<Node> paneChildren = pane.getChildren();

                        Node view = pair.getKey().getView();

                        if (paneChildren.contains(view)) return;

                        paneChildren.add(view);
                    });
                })
        );
    }

    private void triggerRemove(Receipt receipt) {
        this.expenseItemViewManager.removeHandler(receipt)
                .ifPresent(pair -> handleAnimationOn(pair, -1, e -> {
                    NullableUtils.executeNonNull(this.parentPane,
                            paneSupplier -> paneSupplier.get().getChildren()
                                    .remove(pair.getKey().getView())
                    );
                }));
    }

    private void handleAnimationOn(
            Pair<IItemViewHandler<Receipt, Node>, Transition> handlerEntry,
            int interval,
            EventHandler<ActionEvent> onFinished
    ) {
        Transition transition = handlerEntry.getValue();
        transition.stop();

        transition.setOnFinished(onFinished);

        transition.setRate(interval);
        transition.playFromStart();
    }
}
