package org.flintcore.utilities.properties;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import lombok.NonNull;

import java.util.Objects;

public final class PropertyListenerUtils {
    private PropertyListenerUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static <T> void bindSize(
            @NonNull final Property<? extends ObservableList<? extends T>> listProperty,
            @NonNull final IntegerProperty sizeProperty
    ) {
        listProperty.subscribe(list -> {
            if (Objects.isNull(list)) {
                sizeProperty.unbind();
                return;
            }

            sizeProperty.bind(Bindings.size(list));
        });
    }
}
