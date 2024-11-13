package org.flintcore.excel_expenses.excels_handler.models.lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public record SerialListHolder<E extends Serializable>(Collection<E> values)
        implements Serializable {
    public SerialListHolder() {
        this(new ArrayList<>());
    }

    public static <T extends Serializable> SerialListHolder<T> from(
            Collection<T> collection
    ) {
        if (Objects.isNull(collection)) return new SerialListHolder<>();

        if (collection instanceof Serializable) return new SerialListHolder<>(collection);

        return new SerialListHolder<>(new ArrayList<>(collection));
    }
}
