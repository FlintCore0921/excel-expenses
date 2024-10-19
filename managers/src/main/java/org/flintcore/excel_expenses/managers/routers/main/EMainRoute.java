package org.flintcore.excel_expenses.managers.routers.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.IRoute;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
public enum EMainRoute implements IRoute {
    HOME("/templates/Homepage.fxml", null),
    EXPENSES("/templates/expensePage.fxml", null),
    LOCALS("", null),
    REGISTER("", null);

    public final String route;
    private final CompoundResourceBundle.Bundles[] bundlePaths;

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
