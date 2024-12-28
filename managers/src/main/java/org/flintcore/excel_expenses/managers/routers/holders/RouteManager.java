package org.flintcore.excel_expenses.managers.routers.holders;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.flintcore.excel_expenses.managers.routers.IRoute;

import java.util.Stack;

public final class RouteManager<R extends IRoute> implements IRouteManager<R> {

    public static final int MINIMUM_INDEX = 1;
    private final Stack<R> routeHistory;

    public RouteManager(R route) {
        routeHistory = new Stack<>();
        routeHistory.push(route);
    }

    @Override
    public void navigateTo(@NonNull R route) {
        if(route.equals(this.currentRoute())) return;

        this.routeHistory.remove(route);
        this.routeHistory.push(route);
    }

    /**
     * Evaluate if it there enough routes to move backwards.
     */
    @Override
    public boolean canNavigateBack() {
        return this.routeHistory.size() > MINIMUM_INDEX;
    }

    /**
     * Checks if route is inside the manager, and can move back to it.
     *
     * @return true if route is inside, and not the current route.
     */
    @Override
    public boolean canNavigateBackTo(@NonNull R route) {
        return this.canNavigateBack() &&
                this.routeHistory.contains(route) &&
                this.currentRoute() != route;
    }

    /**
     * Checks if route is the same as provided one.
     */
    @Override
    public boolean isCurrentRoute(@NonNull R route) {
        return !this.routeHistory.isEmpty() && this.routeHistory.peek().equals(route);
    }

    @Nullable
    @Override
    public R currentRoute() {
        return !this.routeHistory.isEmpty() ? this.routeHistory.peek() : null;
    }

    @Nullable
    @Override
    public R previousRoute() {
        return getRouteBehindCurrentAt(MINIMUM_INDEX);
    }

    /**
     * {@param step} must be higher or equals to 1.
     */
    @Nullable
    private R getRouteBehindCurrentAt(final int step) {
        if (!canNavigateBack()) return null;

        final int size = this.routeHistory.size();
        var routeStep = Math.max(size - step, size - MINIMUM_INDEX) - 1;

        return this.routeHistory.get(routeStep);
    }
}
