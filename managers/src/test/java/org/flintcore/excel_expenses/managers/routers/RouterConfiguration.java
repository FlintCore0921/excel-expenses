package org.flintcore.excel_expenses.managers.routers;

import org.flintcore.excel_expenses.managers.factories.transitions.RouteTransitionNavigationFactory;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLRouteFactory;
import org.flintcore.excel_expenses.managers.routers.holders.RouteManager;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RouterConfiguration {

    @Bean
    public MainApplicationRouter mainApplicationRouter(
            RouteManager<EMainRoute> rManager,
            FXMLRouteFactory fxmFactory,
            RouteTransitionNavigationFactory transitionNavFactory

    ) {
        return new MainApplicationRouter(rManager, fxmFactory, transitionNavFactory, null);
    }

    @Bean
    public RouteManager<EMainRoute> mainRouteManager() {
        return new RouteManager<>(EMainRoute.HOME);
    }
}
