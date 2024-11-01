package org.flintcore.excel_expenses.managers.routers.configurators;

import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.builders.FXMLFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterBeanConfiguration {

    @Bean("routeViewFactory")
    public FXMLFactory routerViewFactory(
            ApplicationContext applicationContext,
            CompoundResourceBundle bundles
    ) {
        return new FXMLFactory(applicationContext, bundles);
    }
}
