package org.flintcore.excel_expenses.excels_handler.managers.holders;

import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.excels_handler.managers.factories.views.receipts.ExpenseItemPreviewFactory;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Class to handle the list of items handlers of the expenses.
 */
@Log4j2
@Component
public class ExpenseItemViewManager
        extends AbstractExpenseItemViewManager<IItemViewHandler<Receipt, Node>, Transition> {

    protected ObservableMap<IItemViewHandler<Receipt, Node>, Transition> expenseItemHandlers;

    public ExpenseItemViewManager(ExpenseItemPreviewFactory expenseItemFactory) {
        super(expenseItemFactory);
    }

    // TODO create map that listen changes.
    public ObservableMap<IItemViewHandler<Receipt, Node>, Transition> getExpenseHandlers() {
        initProperties();
        return this.expenseItemHandlers;
    }

    protected void initProperties() {
        // Set items handlers, view and value holder to preview.
        NullableUtils.executeIsNull(this.expenseItemHandlers,
                () -> this.expenseItemHandlers = FXCollections.observableHashMap()
        );
    }

    public void clear() {
        NullableUtils.executeNonNull(this.expenseItemHandlers, Map::clear);
    }

    @Override
    public ObservableMap<IItemViewHandler<Receipt, Node>, Transition> getExpenseItemHandlers() {
        return expenseItemHandlers;
    }
}
