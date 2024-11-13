package org.flintcore.excel_expenses.excels_handler.models.properties;

import javafx.beans.property.SimpleObjectProperty;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Deprecated
public final class SelfTransformProperty<T> extends SimpleObjectProperty<T> {
    private final AtomicBoolean isUpdating;
    private final Function<T, ? extends T> mapper;

    public SelfTransformProperty(Function<T, ? extends T> mapper) {
        this.mapper = mapper;
        isUpdating = new AtomicBoolean(false);
    }
}
