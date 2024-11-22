package org.flintcore.excel_expenses.managers.routers;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.exceptions.ErrorConsumerHandler;
import org.flintcore.excel_expenses.managers.factories.transitions.RouteTransitionNavigationFactory;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLRouteFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

@Component
@Log4j2
public class ApplicationRouter implements IApplicationRouter<IRoute> {

    public static final int ENTER_MILLIS_ANIMATION = 500;
    public static final int EXIT_MILLIS_ANIMATION = 200;

    private final RouteManager<IRoute> routeManager;
    private final FXMLRouteFactory fxmlFactory;
    private final RouteTransitionNavigationFactory transitionFactory;
    @Getter
    private final ErrorConsumerHandler errorConsumerHandler;

    private BiFunction<Node, @NonNull Node, Transition> onNavigateTransition;
    private BiFunction<Node, @NonNull Node, Transition> onNavigateBackTransition;

    @Setter
    private Pane parentContainer;

    public ApplicationRouter(ApplicationContext applicationContext,
                             RouteManager<IRoute> routeManager,
                             @Qualifier("routeViewFactory") FXMLRouteFactory fxmlFactory,
                             RouteTransitionNavigationFactory transitionFactory,
                             ErrorConsumerHandler errorConsumerHandler) {
        this.routeManager = routeManager;
        this.fxmlFactory = fxmlFactory;
        this.errorConsumerHandler = errorConsumerHandler;
        this.transitionFactory = transitionFactory;
    }

    @PostConstruct
    public void onInit() {
        this.onNavigateTransition = (prev, curr) -> {
            curr.opacityProperty().set(0.0);

            ParallelTransition transition = new ParallelTransition(
                    this.transitionFactory.createParallelTransition(
                            curr, Duration.millis(ENTER_MILLIS_ANIMATION),
                            List.of(this.transitionFactory.getSwitchEnterTransition())
                    )
            );

            NullableUtils.executeNonNull(prev, node -> {
                Transition exitTransition = this.transitionFactory.createParallelTransition(
                        node, Duration.millis(EXIT_MILLIS_ANIMATION),
                        List.of(this.transitionFactory.getSwitchExitTransition())
                );

                transition.getChildren().add(exitTransition);
            });

            return transition;
        };
        this.onNavigateBackTransition = (prev, curr) -> {
            ParallelTransition transition = new ParallelTransition(
                    this.transitionFactory.createSlideInHorizontallyTransition(
                            curr, Duration.millis(ENTER_MILLIS_ANIMATION), () -> 1.0
                    )
            );

            NullableUtils.executeNonNull(prev, node -> {
                Transition exitTransition = this.transitionFactory.createParallelTransition(
                        node, Duration.millis(EXIT_MILLIS_ANIMATION),
                        List.of(this.transitionFactory.getSwitchExitTransition())
                );

                transition.getChildren().add(exitTransition);
            });

            return transition;
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
    public void navigateTo(IRoute route,
                           @NonNull BiFunction<Node, @NonNull Node, Transition> onRoutesUpdate
    ) {
        if (Objects.isNull(this.parentContainer)) {
            this.errorConsumerHandler.accept(
                    new NullPointerException("Parent view not accessible to proceed")
            );
            return;
        }

        if (this.routeManager.isCurrentRoute(route)) {
            return;
        }

        this.routeManager.navigateTo(route);

        setView(route, onRoutesUpdate);
    }

    /**
     * Build the route as a Node view, in case not built just ignore it.
     */
    private void setView(IRoute route, BiFunction<Node, @NonNull Node, Transition> onRoutesUpdate) {
        Optional<Node> nodeBuilt = this.fxmlFactory.buildLoader(route);

        if (nodeBuilt.isEmpty()) {
            log.info("The view was not built");
            return;
        }

        Node currentNode = nodeBuilt.get();
        List<Node> childrenContainer = this.parentContainer.getChildren();
        Node previousNode = childrenContainer.isEmpty() ? null : childrenContainer.get(0);

        log.info("Adding node into the parent pane...");
        childrenContainer.add(currentNode);

        Transition transition = onRoutesUpdate.apply(previousNode, currentNode);
        transition.setOnFinished(evt ->
                NullableUtils.executeNonNull(previousNode, childrenContainer::remove)
        );
        log.info("Showing node view...");
        transition.play();
    }

    @Override
    public void navigateBack() {
        if (!this.routeManager.canNavigateBack()) return;

        IRoute previousRoute = this.routeManager.previousRoute();
        this.navigateTo(previousRoute, this.onNavigateBackTransition);
    }
}
