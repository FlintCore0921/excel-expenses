package org.flintcore.excel_expenses.managers.routers.routes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle.Bundles;
import org.flintcore.excel_expenses.managers.routers.IRoute;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum EMainRoute implements IRoute {
    HOME(MAIN_ROUTE + "/home-view.fxml", new Bundles[] {
            Bundles.ACTIONS
    });

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
