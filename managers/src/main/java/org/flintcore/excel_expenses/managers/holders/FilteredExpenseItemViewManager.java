package org.flintcore.excel_expenses.managers.holders;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.managers.factories.views.receipts.ExpenseItemPreviewFactory;
import org.flintcore.excel_expenses.models.observables.collections.FilteredMap;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Class to handle the list of items handlers of the expenses.
 */
@Log4j2
@Component
public class FilteredExpenseItemViewManager
        extends AbstractExpenseItemViewManager<IItemViewHandler<Receipt, Node>, Transition> {
    protected FilteredMap<IItemViewHandler<Receipt, Node>, Transition> expenseItemHandlers;


    public FilteredExpenseItemViewManager(ExpenseItemPreviewFactory expenseItemFactory) {
        super(expenseItemFactory);
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

    protected void initProperties() {
        // Set items handlers, view and value holder to preview.
        NullableUtils.executeIsNull(this.expenseItemHandlers,
                () -> this.expenseItemHandlers = FilteredMap.selfManagedFiltered()
        );
    }

    @Override
    public FilteredMap<IItemViewHandler<Receipt, Node>, Transition> getExpenseItemHandlers() {
        return expenseItemHandlers;
    }
}
