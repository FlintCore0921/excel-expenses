package org.flintcore.excel_expenses.excels_handler.managers.factories.views;

import javafx.scene.Node;

import java.util.function.Consumer;

public interface IItemViewHandler<T, N extends Node> {
    void setValue(T value);

    T getValue();

    N getView();

    void setOnEdit(Consumer<T> action);

    void setOnRemove(Consumer<T> action);
}
