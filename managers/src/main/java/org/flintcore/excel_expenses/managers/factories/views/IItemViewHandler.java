package org.flintcore.excel_expenses.managers.factories.views;

import javafx.scene.Node;

public interface IItemViewHandler<T, N extends Node> {
    void setValue(T value);
    T getValue();
    N getView();
}
