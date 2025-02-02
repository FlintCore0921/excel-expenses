package org.flintcore.excel_expenses.managers.routers.factories;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.IRoute;
import org.flintcore.excel_expenses.models.NodeWrapper;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class FXMLRouteFactoryImpl<L extends IRoute, R> implements IFXMLFactory<L, R> {
    private final ApplicationContext appContext;
    protected final CompoundResourceBundle bundleManager;

    public FXMLRouteFactoryImpl(
            ApplicationContext appContext,
            CompoundResourceBundle bundleManager
    ) {
        this.appContext = appContext;
        this.bundleManager = bundleManager;
    }

    protected FXMLLoader prepareLoader(L route) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route.getRoute())
        );

        this.bundleManager.registerBundles(route.getBundlePaths());

        FXMLLoader loader = new FXMLLoader(resource, this.bundleManager);
        // Charset
        loader.setCharset(StandardCharsets.UTF_8);
        loader.setControllerFactory(appContext::getBean);
        return loader;
    }


    protected NodeWrapper<Node, Object> buildWrapperResult(FXMLLoader loader) throws IOException {
        return new NodeWrapper<>(loader.load(), loader.getController());
    }
}
