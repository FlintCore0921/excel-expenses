package org.flintcore.excel_expenses.handlers.routers.main;

import org.flintcore.excel_expenses.handlers.exceptions.ErrorConsumerHandler;
import org.flintcore.excel_expenses.handlers.routers.routers.IRouter;
import org.flintcore.excel_expenses.handlers.routers.RouteManager;
import data.utils.NullableUtils;
import javafx.animation.Transition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class MainViewRouter implements IRouter<EMainRoute> {
    private final ApplicationContext applicationContext;

    private final RouteManager<EMainRoute> routerManager;

    @Getter
    private final ErrorConsumerHandler errorConsumerHandler;

    public MainViewRouter(
            ApplicationContext applicationContext,
            ErrorConsumerHandler errorConsumerHandler,
            RouteManager<EMainRoute> routerManager) {
        this.applicationContext = applicationContext;
        this.errorConsumerHandler = errorConsumerHandler;
        this.routerManager = routerManager;
    }

    public void navigateTo(
            final EMainRoute route,
            Consumer<Node> paneConsumer,
            Function<Node, Transition> transition
    ) {
        if(this.routerManager.isCurrentRoute(route)) {
            return;
        }

        routerManager.navigateTo(route);

        URL resource = Objects.requireNonNull(
                getClass().getResource(route.route)
        );

        FXMLLoader loader = new FXMLLoader(resource);
        loader.setControllerFactory(applicationContext::getBean);

        try {
            Node ndLoaded = loader.load();
            NullableUtils.executeNonNull(paneConsumer, () -> paneConsumer.accept(ndLoaded));

            NullableUtils.executeNonNull(
                    transition,
                    tr -> tr.apply(ndLoaded).play()
            );
        } catch (IOException e) {
            this.errorConsumerHandler.accept(e);
        }
    }

    public void navigateTo(final EMainRoute route, Consumer<Node> paneConsume) {
        this.navigateTo(route, paneConsume, null);
    }

    public void navigateBack(Consumer<Node> paneConsumer, Function<Node, Transition> transition) {
        this.routerManager.navigateBack();
        this.navigateTo(this.routerManager.currentRoute(), paneConsumer, transition);
    }
}
