package org.flintcore.excel_expenses.managers.holders;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.util.Pair;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.managers.factories.views.receipts.ExpenseItemPreviewFactory;
import org.flintcore.excel_expenses.models.observables.collections.FilteredMap;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class to handle the list of items handlers of the expenses.
 */
@Log4j2
@Component
public class ExpenseItemViewManager {
    protected final ExpenseItemPreviewFactory expenseItemFactory;

    protected FilteredMap<IItemViewHandler<Receipt, Node>, Transition> expenseItemHandlers;
    @Setter
    protected Function<Node, Transition> transitionBuilder;

    public ExpenseItemViewManager(ExpenseItemPreviewFactory expenseItemFactory) {
        this.expenseItemFactory = expenseItemFactory;
    }

    // TODO create map that listen changes.
    public ObservableMap<IItemViewHandler<Receipt, Node>, Transition> getExpenseHandlers() {
        initProperties();
        return this.expenseItemHandlers;
    }

    public void clear() {
        NullableUtils.executeNonNull(this.expenseItemHandlers, Map::clear);
    }

    /**
     * Create a new unmodifiable list based on the available data by filter assigned.
     *
     * @return a new unmodifiable list with the current values.
     */
    public Optional<Pair<IItemViewHandler<Receipt, Node>, Transition>> getHandler(Receipt receipt) {
        initProperties();

        return this.expenseItemHandlers.entrySet().stream()
                .filter(handler -> Objects.equals(handler.getKey().getValue(), receipt))
                .findFirst()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

    public void createNewHandler(Receipt receipt) {
        IItemViewHandler<Receipt, Node> handler;

        if (getHandler(receipt).isPresent()) return;

        Optional<? extends IItemViewHandler<Receipt, Node>> viewHandler = this.expenseItemFactory
                .buildView();

        if (viewHandler.isEmpty()) {
            log.warn("Unable to create item receipt {} for holder {}",
                    receipt, this.getClass().getSimpleName()
            );
            return;
        }

        // Assign found value to use it.
        // Add item to apply it.
        handler = viewHandler.get();

        // Set the receipt values.
        handler.setValue(receipt);

        // Create Transitions
        assignTransitionOf(handler);
    }

    public Optional<Pair<IItemViewHandler<Receipt, Node>, Transition>> removeHandler(Receipt receipt) {
        if (Objects.isNull(this.expenseItemHandlers)) return Optional.empty();

        Optional<Pair<IItemViewHandler<Receipt, Node>, Transition>> optionalEntry = this.expenseItemHandlers
                .entrySet()
                .stream()
                .filter(handler -> Objects.equals(handler.getKey().getValue(), receipt))
                .findFirst()
                .map(handler -> new Pair<>(handler.getKey(), handler.getValue()));

        optionalEntry.ifPresent(entry -> this.expenseItemHandlers.remove(entry.getKey()));

        return optionalEntry;
    }

    /**
     * Set a predicate based on the key of values. Will override the previous filter.
     *
     * @see #setReceiptFilter(Predicate)
     */
    public void setKeyFilter(Predicate<IItemViewHandler<Receipt, Node>> predicate) {
        this.expenseItemHandlers.filterByKey(predicate);
    }

    /**
     * Set a predicate based on the key of values. Will override the previous filter.
     *
     * @see #setKeyFilter(Predicate)
     */
    public void setReceiptFilter(Predicate<Receipt> predicate) {
        this.setKeyFilter(handler -> predicate.test(handler.getValue()));
    }

    /**
     * Set a predicate based on the key of values. Will override the previous filter.
     */
    public void cleanFilter() {
        this.expenseItemHandlers.filterBy(null);
    }

    private void assignTransitionOf(IItemViewHandler<Receipt, Node> handler) {
        if (Objects.isNull(handler)) {
            log.warn("Handler was not available to create Transition.");
            return;
        }

        Node view = handler.getView();

        final Transition showTransition = this.transitionBuilder.apply(view);

        if (Objects.isNull(showTransition)) return;

        this.expenseItemHandlers.putIfAbsent(handler, showTransition);
    }

    protected void initProperties() {
        // Set items handlers, view and value holder to preview.
        NullableUtils.executeIsNull(this.expenseItemHandlers,
                () -> this.expenseItemHandlers = FilteredMap.selfManagedFiltered()
        );
    }
}
