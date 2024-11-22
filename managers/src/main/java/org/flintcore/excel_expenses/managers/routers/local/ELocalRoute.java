package org.flintcore.excel_expenses.managers.routers.local;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

@AllArgsConstructor
@Getter
public enum ELocalRoute implements IRoute {
    CREATE(
            "/templates/createLocalBusinessForm.fxml",
            new Bundles[]{Bundles.LOCAL_MESSAGES}
    );
    private final String route;
    private final Bundles[] bundlePaths;

    @Override
    public int getOrder() {
        return ordinal();
    }
}
