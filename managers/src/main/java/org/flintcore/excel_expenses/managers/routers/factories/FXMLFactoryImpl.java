package org.flintcore.excel_expenses.managers.routers.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
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

    protected NodeWrapper<Node, Object> buildWrapperResult(FXMLLoader loader) throws IOException {
        return new NodeWrapper<>(loader.load(), loader.getController());
    }
}
