package org.flintcore.excel_expenses.excels_handler.managers.routers;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class RouteManager<T> {

    public static final int MIN_INDEX_RETRIEVE = 1;
    private Stack<T> routeHistory;

    @PostConstruct
    public void init() {
        routeHistory = new Stack<>();
    }

    public void navigateTo(T route) {
        this.routeHistory.remove(route);
        this.routeHistory.push(route);
    }

    public boolean canNavigateBack() {
        return this.routeHistory.size() > 1;
    }

    public boolean canNavigateBackTo(T route) {
        return canNavigateBack() &&
                this.routeHistory.contains(route) && this.currentRoute() != route;
    }

    public boolean isCurrentRoute(T route) {
        return !this.routeHistory.isEmpty() && this.routeHistory.peek().equals(route);
    }

    @Nullable
    public T currentRoute() {
        return !this.routeHistory.isEmpty() ? this.routeHistory.peek() : null;
    }

    @Nullable
    public T previousRoute() {
        return previousRoute(MIN_INDEX_RETRIEVE);
    }

    /**
     * {@param position} must be higher or equals to 1.
     */
    @Nullable
    public T previousRoute(int position) {
        if (this.routeHistory.isEmpty()) return null;

        position = Math.max(position, MIN_INDEX_RETRIEVE);

        if (position >= this.routeHistory.size()) {
            position = MIN_INDEX_RETRIEVE;
        }

        return this.routeHistory.get(
                this.routeHistory.size() - (position + 1)
        );
    }
}
