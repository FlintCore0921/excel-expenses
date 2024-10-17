package org.flintcore.excel_expenses.managers.routers.local;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.routers.IRoute;

@AllArgsConstructor
@Getter
public enum ELocalRoute implements IRoute {
    CREATE("/templates/createLocalBusinessForm.fxml");
    private final String route;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
