package org.flintcore.excel_expenses.listeners.keyboards;

import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * Up and Down Movement
 */
@RequiredArgsConstructor
public class ComboBoxRemoteKeyListener<T> implements EventHandler<KeyEvent> {
    private final ComboBox<T> comboBox;
    private int selectedIndex;

    @Override
    public void handle(KeyEvent evt) {
        KeyCode code = evt.getCode();
        switch (code) {
            case UP -> this.selectedIndex = Math.max(0, --selectedIndex);
            case DOWN -> this.selectedIndex = Math.min(getComboItemSize(), ++selectedIndex);
            case ENTER -> this.comboBox.getSelectionModel().select(this.selectedIndex);
        }

        this.comboBox.show();
    }

    private int getComboItemSize() {
        return this.comboBox.getItems().size();
    }
}
