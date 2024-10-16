package org.flintcore.excel_expenses.listeners.keyboards;

import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.AllArgsConstructor;

/**Up and Down Movement*/
@AllArgsConstructor
public class ComboBoxRemoteKeyListener<T> implements EventHandler<KeyEvent> {
    private final ComboBox<T> comboBox;

    @Override
    public void handle(KeyEvent evt) {
        if(!this.comboBox.isShowing()) {
            this.comboBox.show();
        }

        KeyCode code = evt.getCode();
        switch (code) {
            case UP -> this.comboBox.getSelectionModel().selectPrevious();
            case DOWN -> this.comboBox.getSelectionModel().selectNext();
        }
    }
}
