package org.flintcore.excel_expenses.handlers.routers.routers;

import org.flintcore.excel_expenses.handlers.exceptions.ErrorConsumerHandler;
import org.flintcore.excel_expenses.handlers.routers.IRoute;
import org.flintcore.excel_expenses.handlers.routers.RouteManager;
import org.flintcore.excel_expenses.views.utils.RouteTransitionNavigationFactory;
import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
public class ApplicationRouter implements IApplicationRouter<IRoute> {

    public static final int ENTER_MILLIS_ANIMATION = 500;
    public static final int EXIT_MILLIS_ANIMATION = 200;


    private final RouteManager<IRoute> routeManager;
    private final ApplicationContext applicationContext;
    private final RouteTransitionNavigationFactory transitionFactory;
    @Getter
    private final ErrorConsumerHandler errorConsumerHandler;

    private BiFunction<Node, @NonNull Node, Transition> onNavigateTransition;

    @Getter
    @Setter
    private Pane parentContainer;

    public ApplicationRouter(ApplicationContext applicationContext,
                             RouteManager<IRoute> routeManager,
                             RouteTransitionNavigationFactory transitionFactory,
                             ErrorConsumerHandler errorConsumerHandler) {
        this.routeManager = routeManager;
        this.applicationContext = applicationContext;
        this.errorConsumerHandler = errorConsumerHandler;
        this.transitionFactory = transitionFactory;
    }

    @PostConstruct
    public void onInit() {
        this.onNavigateTransition = (prev, curr) -> {
            curr.opacityProperty().set(0.0);

            SequentialTransition sequentialTransition = new SequentialTransition(
                    this.transitionFactory.createParallelTransition(
                            curr, Duration.millis(ENTER_MILLIS_ANIMATION),
                            List.of(this.transitionFactory.getSwitchEnterTransition())
                    )
            );

            NullableUtils.executeNonNull(prev, node -> this.transitionFactory.createParallelTransition(
                    prev, Duration.millis(EXIT_MILLIS_ANIMATION),
                    List.of(this.transitionFactory.getSwitchExitTransition())
            ));

            return sequentialTransition;
        };
    }

    @Override
    public void navigateTo(IRoute route) {
        navigateTo(route, onNavigateTransition);
    }

    /**
     * @param onRoutesUpdate configurator and transition trigger to set in screen.
     * @apiNote In the {@code onRoutesUpdate} function, is it recommended to configure the initial properties of the view.
     */
    @Override
    public void navigateTo(
            IRoute route,
            @NonNull BiFunction<Node, @NonNull Node, Transition> onRoutesUpdate
    ) {
        if (Objects.isNull(this.parentContainer)) {
            // TODO
            this.errorConsumerHandler.accept(
                    new NullPointerException("Parent view not accessible to proceed")
            );
            return;
        }

        IRoute previousRoute = this.routeManager.currentRoute();
        this.routeManager.navigateTo(route);

        if (this.routeManager.isCurrentRoute(previousRoute)) {
            return;
        }

        try {
            FXMLLoader loader = buildFML(route);
            Node currentNode = loader.load();
            List<Node> childrenContainer = this.parentContainer.getChildren();
            Node previousNode = childrenContainer.isEmpty() ? null : childrenContainer.get(0);

            childrenContainer.add(currentNode);

            Transition transition = onRoutesUpdate.apply(previousNode, currentNode);
            transition.setOnFinished(evt ->
                    NullableUtils.executeNonNull(previousNode, childrenContainer::remove)
            );
            transition.play();
        } catch (IOException e) {
            this.errorConsumerHandler.accept(e);
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
