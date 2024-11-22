package org.flintcore.excel_expenses.managers.routers.factories;

import javafx.fxml.FXMLLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class FXMLFactoryImpl<L, R> implements IFXMLFactory<L, R> {
    protected final ApplicationContext appContext;

    protected FXMLLoader prepareLoader(String route) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route)
        );

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(appContext::getBean);
        return loader;
    }

}
