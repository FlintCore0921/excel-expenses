package org.flintcore.excel_expenses.handlers.fields.businesses;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.models.expenses.IBusiness;

import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
@Log4j2
public class BusinessSelectionListener<T extends IBusiness> implements Consumer<T> {
    private final TextField localNameField, localRNCField;

    @Override
    public void accept(T business) {
        log.info(business);
        if (Objects.isNull(business)) {
            this.clearDataFromFields();
            return;
        }

        this.localNameField.setText(business.getName());
        this.localRNCField.setText(business.getRNC());
    }

    private void clearDataFromFields() {
        this.localNameField.setText("-- None --");
        this.localRNCField.clear();
    }
}
