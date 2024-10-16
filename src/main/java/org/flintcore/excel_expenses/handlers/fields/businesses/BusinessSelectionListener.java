package org.flintcore.excel_expenses.handlers.fields.businesses;

import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import org.flintcore.excel_expenses.models.expenses.IBusiness;

import java.util.Objects;
import java.util.function.Consumer;

@AllArgsConstructor
public class BusinessSelectionListener implements Consumer<IBusiness> {
    private final TextField localNameField, localRNCField;

    @Override
    public void accept(IBusiness business) {
        if (Objects.isNull(business)) {
            // TODO
            this.localNameField.setText("-- None --");
            this.localRNCField.setText("");
            return;
        }

        this.localNameField.setText(business.getName());
        this.localRNCField.setText(business.getRNC());
    }
}
