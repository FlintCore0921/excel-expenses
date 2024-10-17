package org.flintcore.excel_expenses.models.lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public record SerialListHolder<E extends Serializable>(Collection<E> values)
        implements Serializable {
    public SerialListHolder() {
        this(new ArrayList<>());
    }
}
