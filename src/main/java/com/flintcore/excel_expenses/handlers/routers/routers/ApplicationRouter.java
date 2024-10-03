package com.flintcore.excel_expenses.handlers.routers.routers;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import com.flintcore.excel_expenses.handlers.routers.RouteManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.BiConsumer;

@Component
public class ApplicationRouter implements IApplicationRouter<IRoute> {

    private final RouteManager<IRoute> routeManager;
    private final ApplicationContext applicationContext;

    @Getter
    @Setter
    private Pane parentContainer;

    public ApplicationRouter(ApplicationContext applicationContext, RouteManager<IRoute> routeManager) {
        this.routeManager = routeManager;
        this.applicationContext = applicationContext;
    }

    @Override
    public void navigateTo(IRoute route) {
        this.routeManager.navigateTo(route);
    }

    @Override
    public void navigateTo(IRoute route, BiConsumer<Node, Node> onRoutesUpdate) {
        this.routeManager.navigateTo(route);

        if (!this.routeManager.isCurrentRoute(route)) {
            return;
        }

        FXMLLoader loader = buildFML(route);

        try {
            Node currentNode = loader.load();
            Node previousNode = this.parentContainer.getChildren().get(0);
            onRoutesUpdate.accept(previousNode, currentNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader buildFML(IRoute route) {
        URL resource = Objects.requireNonNull(
                getClass().getResource(route.getRoute())
        );

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(applicationContext::getBean);
        return loader;
    }
}
