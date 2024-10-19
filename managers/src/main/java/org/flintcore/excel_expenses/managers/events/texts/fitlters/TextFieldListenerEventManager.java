package org.flintcore.excel_expenses.managers.events.texts.fitlters;

import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TextFieldListenerEventManager<T> {
    protected final TextField textFilter;
    protected Supplier<FilteredList<T>> itemsSupplier;
    protected SubscriptionHolder subsManager;
    protected Function<T, String> filterComparator;

    public TextFieldListenerEventManager(
            TextField textFilter,
            Supplier<FilteredList<T>> itemsSupplier,
            Function<T, String> filterComparator
    ) {
        this.textFilter = textFilter;
        this.itemsSupplier = itemsSupplier;
        this.subsManager = new SubscriptionHolder();
        this.filterComparator = ObjectUtils.defaultIfNull(filterComparator, Object::toString);
    }

    public TextFieldListenerEventManager(TextField textFilter) {
        this(textFilter, null, null);
    }

    public void setFilterComparator(Function<T, String> filterComparator) {
        this.filterComparator = filterComparator;
        setup();
    }

    public abstract void setup();
}
