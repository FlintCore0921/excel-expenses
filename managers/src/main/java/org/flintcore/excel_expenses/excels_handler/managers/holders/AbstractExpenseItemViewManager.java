package org.flintcore.excel_expenses.excels_handler.managers.holders;

import javafx.animation.Transition;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.excels_handler.managers.factories.views.receipts.ExpenseItemPreviewFactory;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractExpenseItemViewManager<
        K extends IItemViewHandler<Receipt, Node>, V extends Transition> {
    protected final ExpenseItemPreviewFactory expenseItemFactory;

    @Setter
    protected Function<Node, V> transitionBuilder;

    public abstract void clear();

    protected abstract ObservableMap<K, V> getExpenseItemHandlers();

    /**
     * Create a new unmodifiable list based on the available data by filter assigned.
     *
     * @return a new unmodifiable list with the current values.
     */
    public Optional<Pair<K, V>> getHandler(Receipt receipt) {
        initProperties();

        return this.getExpenseItemHandlers().entrySet().stream()
                .filter(handler -> Objects.equals(handler.getKey().getValue(), receipt))
                .findFirst()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()));
    }

    public void createNewHandler(Receipt receipt) {
        K handler;

        if (getHandler(receipt).isPresent()) return;

        Optional<? extends K> viewHandler = this.expenseItemFactory
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

    public Optional<Pair<K, V>> removeHandler(Receipt receipt) {
        if (Objects.isNull(this.getExpenseItemHandlers())) return Optional.empty();

        Optional<Pair<K, V>> optionalEntry = this.getExpenseItemHandlers()
                .entrySet()
                .stream()
                .filter(handler -> Objects.equals(handler.getKey().getValue(), receipt))
                .findFirst()
                .map(handler -> new Pair<>(handler.getKey(), handler.getValue()));

        optionalEntry.ifPresent(entry -> this.getExpenseItemHandlers()
                .remove(entry.getKey())
        );

        return optionalEntry;
    }

    protected void assignTransitionOf(K handler) {
        if (Objects.isNull(handler)) {
            log.warn("Handler was not available to create Transition.");
            return;
        }

        Node view = handler.getView();

        final V showTransition = this.transitionBuilder.apply(view);

        if (Objects.isNull(showTransition)) return;

        this.getExpenseItemHandlers().putIfAbsent(handler, showTransition);
    }

    protected abstract void initProperties();
}
