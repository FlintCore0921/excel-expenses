package org.flintcore.excel_expenses.managers.routers.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

@Getter
@AllArgsConstructor
public enum EExpenseFormRoute implements IRoute {
    LOCAL(MAIN_ROUTE + "/register-local-view.fxml", Bundles.defaultBundles()),
    RECEIPT(MAIN_ROUTE + "/register-receipt-view.fxml", Bundles.defaultBundles()),
    PRICE(MAIN_ROUTE + "/register-price-view.fxml", Bundles.defaultBundles());

    private final String route;
    private final Bundles[] bundlePaths;


    @Override
    public int getOrder() { return ordinal(); }
}
