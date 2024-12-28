package org.flintcore.excel_expenses.managers.events.texts.filters.business;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.models.business.IBusiness;
import org.flintcore.utilities.lists.ObservableListUtils;

/**Just for testing purpose.*/
@Getter
@Log4j2
public class RNCTestListener {
    private final ObservableList<IBusiness> items;
    @Getter(AccessLevel.NONE)
    private final ComboBox<IBusiness> comboBox;

    public RNCTestListener(TextField filter, ComboBox<IBusiness> comboBox) {
        this.comboBox = comboBox;
        items = this.comboBox.getItems();
        comboBox.setItems(ObservableListUtils.wrapInto(items));

        filter.textProperty().subscribe(t -> comboBox.show());

        filter.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case UP -> {
                    this.comboBox.getSelectionModel().selectPrevious();
                }
                case DOWN -> {
                    this.comboBox.getSelectionModel().selectNext();
                }
            }
        });

        comboBox.valueProperty().subscribe(t -> log.info("Data: {}", t));
    }
}
