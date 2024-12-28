package org.flintcore.excel_expenses.managers.routers.holders;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.flintcore.excel_expenses.managers.routers.IRoute;

public interface IRouteManager<R extends IRoute> {
    void navigateTo(@NonNull R route);
    boolean canNavigateBack();
    boolean canNavigateBackTo(@NonNull R route);
    boolean isCurrentRoute(@NonNull R route);
    R currentRoute();
    @Nullable
    R previousRoute();
}
