package com.flintcore.excel_expenses.handlers.routers;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class RouteManager<T> {

    private Stack<T> routeHistory;

    @PostConstruct
    public void init() {
        routeHistory = new Stack<>();
    }

    public Stack<T> getRouteHistory() {
        Stack<T> copyRoute = new Stack<>();
        copyRoute.addAll(routeHistory);
        return copyRoute;
    }

    public void navigateTo(T route) {
        this.routeHistory.remove(route);
        this.routeHistory.push(route);
    }

    public void navigateBack() {

    }

    public void navigateBackTo(T route) {
        if (this.routeHistory.isEmpty() || !this.routeHistory.contains(route)) {
            return;
        }

        while (!this.routeHistory.peek().equals(route)) {
            this.routeHistory.pop();
        }
    }

    public boolean isCurrentRoute(T route) {
        return !this.routeHistory.isEmpty() || this.routeHistory.peek().equals(route);
    }

    public T currentRoute() {
        return this.routeHistory.peek();
    }
}
