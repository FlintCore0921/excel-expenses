package org.flintcore.excel_expenses.managers.events.texts.filters;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.flintcore.excel_expenses.models.subscriptions.SubscriptionHolder;

import java.util.function.Function;

public abstract class TextFilterListenerManager<T> {
    protected final StringProperty textFilterProperty;
    protected final ObjectProperty<FilteredList<T>> itemsFilteredProperty;
    protected SubscriptionHolder subsManager;
    // Update filter comparator
    @Setter
    protected Function<T, String> filterComparator;

    public TextFilterListenerManager(
            @NonNull StringProperty textFilter,
            ObjectProperty<FilteredList<T>> itemsSupplierProperty,
            Function<T, String> filterComparator
    ) {
        this.textFilterProperty = new SimpleStringProperty();
        this.textFilterProperty.bind(textFilter);
        this.itemsFilteredProperty = ObjectUtils.defaultIfNull(
                itemsSupplierProperty,
                new SimpleObjectProperty<>(this, null, null)
        );
        this.filterComparator = ObjectUtils.defaultIfNull(filterComparator, Object::toString);
        this.subsManager = new SubscriptionHolder();
    }

    public TextFilterListenerManager(TextField textFilter, FilteredList<T> listSupplier, Function<T, String> filterComparator) {
        this(textFilter.textProperty(), null, filterComparator);
        this.itemsFilteredProperty.setValue(listSupplier);
    }

    public TextFilterListenerManager(TextField textFilter, FilteredList<T> listSupplier) {
        this(textFilter.textProperty(), null, null);
        this.itemsFilteredProperty.setValue(listSupplier);
    }

    public TextFilterListenerManager(TextField textFilter) {
        this(textFilter, null);
    }

    /** This code is required to call, superclass does not use it.*/
    protected abstract void setup();
}
