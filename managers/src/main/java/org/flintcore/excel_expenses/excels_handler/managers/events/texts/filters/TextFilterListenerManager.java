package org.flintcore.excel_expenses.excels_handler.managers.events.texts.filters;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.SubscriptionHolder;
import org.flintcore.utilities.properties.PropertyListenerUtils;

import java.io.Closeable;
import java.util.function.Function;

public abstract class TextFilterListenerManager<T> implements Closeable {
    protected TextField textFilter;
    protected StringProperty textFilterProperty;
    protected final ObjectProperty<FilteredList<T>> itemsFilteredProperty;
    protected final IntegerProperty itemsFilteredSizeProperty;
    protected final ObjectProperty<ObservableList<? extends T>> mainListProperty;
    protected final IntegerProperty mainListSizeProperty;

    protected SubscriptionHolder subsManager;

    // Update filter comparator
    @Setter
    protected Function<T, String> filterComparator;

    public TextFilterListenerManager(
            @NonNull TextField textFilter,
            ObjectProperty<FilteredList<T>> itemsSupplierProperty,
            Function<T, String> filterComparator
    ) {
        this.textFilter = textFilter;
        this.textFilterProperty = textFilter.textProperty();

        this.itemsFilteredProperty = ObjectUtils.defaultIfNull(
                itemsSupplierProperty,
                new SimpleObjectProperty<>(this, null, null)
        );
        this.itemsFilteredSizeProperty = new SimpleIntegerProperty();

        this.mainListProperty = new SimpleObjectProperty<>();
        this.mainListProperty.bind(
                this.itemsFilteredProperty.map(FilteredList::getSource)
        );
        this.mainListSizeProperty = new SimpleIntegerProperty();

        this.filterComparator = ObjectUtils.defaultIfNull(filterComparator, Object::toString);
        this.subsManager = new SubscriptionHolder();

        applySizeProperty();
    }


    public TextFilterListenerManager(@NonNull TextField textFilter, FilteredList<T> listSupplier) {
        this(textFilter, null, null);
        this.itemsFilteredProperty.setValue(listSupplier);
    }

    public TextFilterListenerManager(@NonNull TextField textFilter) {
        this(textFilter, null);
    }

    private void applySizeProperty() {
        // Filtered list
        PropertyListenerUtils.bindSize(this.itemsFilteredProperty, this.itemsFilteredSizeProperty);
        // Source list
        PropertyListenerUtils.bindSize(this.mainListProperty, this.mainListSizeProperty);
    }

    /**
     * This code is required to call, superclass does not use it.
     */
    protected abstract void setup();
}
