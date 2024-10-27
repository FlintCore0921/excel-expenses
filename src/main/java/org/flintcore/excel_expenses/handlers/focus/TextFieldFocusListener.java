package org.flintcore.excel_expenses.handlers.focus;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class TextFieldFocusListener implements ChangeListener<Boolean> {
    private final Runnable onAction;

    public TextFieldFocusListener(Runnable onAction) {
        this.onAction = onAction;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) { onAction.run(); }
    }
}
