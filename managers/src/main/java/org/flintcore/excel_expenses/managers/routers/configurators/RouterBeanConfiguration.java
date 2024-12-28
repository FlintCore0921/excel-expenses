package org.flintcore.excel_expenses.managers.routers.configurators;

import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.holders.RouteManager;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLRouteFactory;
import org.flintcore.excel_expenses.managers.routers.holders.IRouteManager;
import org.flintcore.excel_expenses.managers.routers.routes.EExpenseFormRoute;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterBeanConfiguration {

    @Bean("routeViewFactory")
    public FXMLRouteFactory routerViewFactory(
            ApplicationContext applicationContext,
            CompoundResourceBundle bundles
    ) {
        return new FXMLRouteFactory(applicationContext, bundles);
    }

    @Bean
    public IRouteManager<EMainRoute> mainRouteManager() {
        return new RouteManager<>(EMainRoute.HOME);
    }

    @Bean
    public IRouteManager<EExpenseFormRoute> expenseFormRouteManager() {
        return new RouteManager<>(EExpenseFormRoute.LOCAL);
    }
}
