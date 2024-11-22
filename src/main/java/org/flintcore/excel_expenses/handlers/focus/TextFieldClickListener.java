package org.flintcore.excel_expenses.handlers.focus;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

public class TextFieldClickListener implements ChangeListener<Node> {
    private final Runnable onAction;

    public TextFieldClickListener(Runnable onAction) {
        this.onAction = onAction;
    }

    @Override
    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
        onAction.run();
    }
}
