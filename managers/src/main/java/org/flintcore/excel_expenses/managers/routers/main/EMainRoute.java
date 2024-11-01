package org.flintcore.excel_expenses.managers.routers.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum EMainRoute implements IRoute {
    HOME("/templates/Homepage.fxml", Bundles.getDefaultBundles()),
    EXPENSES("/templates/expensePage.fxml",  Bundles.getDefaultBundles()),
    LOCALS("",  Bundles.getDefaultBundles()),
    REGISTER("",  Bundles.getDefaultBundles());

    public final String route;
    private final Bundles[] bundlePaths;

    public static List<EMainRoute> availableRoutes() {
        return Arrays.stream(values())
                .filter(rt -> Objects.isNull(rt) || !rt.getRoute().isBlank())
                .toList();
    }

    @Override
    public int getOrder() {
        return ordinal();
    }
}
