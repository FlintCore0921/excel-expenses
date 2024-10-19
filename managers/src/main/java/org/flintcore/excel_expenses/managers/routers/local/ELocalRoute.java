package org.flintcore.excel_expenses.managers.routers.local;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.IRoute;

@AllArgsConstructor
@Getter
public enum ELocalRoute implements IRoute {
    CREATE(
            "/templates/createLocalBusinessForm.fxml",
            null
    );
    private final String route;
    private final CompoundResourceBundle.Bundles[] bundlePaths;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
