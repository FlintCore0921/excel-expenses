package org.flintcore.excel_expenses.managers.routers;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.flintcore.excel_expenses.managers.exceptions.handlers.ErrorConsumerHandler;
import org.flintcore.excel_expenses.managers.factories.transitions.RouteTransitionNavigationFactory;
import org.flintcore.excel_expenses.managers.routers.factories.nodes.FXMLRouteFactory;
import org.flintcore.excel_expenses.managers.routers.holders.IRouteManager;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

//@Profile({"dev", "prod"})
@Component
@Primary
@Log4j2
public final class MainApplicationRouter implements IApplicationRouter<EMainRoute> {

    private static final int ENTER_MILLIS_ANIMATION = 500;
    private static final int EXIT_MILLIS_ANIMATION = 200;

    private final IRouteManager<EMainRoute> routeManager;
    private final FXMLRouteFactory fxmlFactory;
    private final RouteTransitionNavigationFactory transitionFactory;
    @Getter
    private final ErrorConsumerHandler errorConsumerHandler;

    private final AtomicReference<Pair<List<Node>, Transition>> cancelNavHolder;

    // On first load of app, must trigger init view.
    private boolean firstLoad;

    private BiFunction<Node, @NonNull Node, Transition> onNavigateTransition;
    private BiFunction<Node, @NonNull Node, Transition> onNavigateBackTransition;

    @Setter
    private Pane parentContainer;

    public MainApplicationRouter(
            IRouteManager<EMainRoute> routeManager,
            @Qualifier("routeViewFactory") FXMLRouteFactory fxmlFactory,
            RouteTransitionNavigationFactory transitionFactory,
            ErrorConsumerHandler errorConsumerHandler) {
        this.routeManager = routeManager;
        this.fxmlFactory = fxmlFactory;
        this.errorConsumerHandler = errorConsumerHandler;
        this.transitionFactory = transitionFactory;

        this.cancelNavHolder = new AtomicReference<>();
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
    public void navigateTo(EMainRoute route) {
        navigateTo(route, onNavigateTransition);
    }

    /**
     * @param onRoutesUpdate configurator and transition trigger to set in screen.
     * @apiNote In the {@code onRoutesUpdate} function, is it recommended to configure the initial properties of the view.
     */
    @Override
    public void navigateTo(
            EMainRoute route,
            @NonNull BiFunction<Node, @NonNull Node, Transition> onRoutesUpdate) {
        if (Objects.isNull(this.parentContainer)) {
            NullableUtils.executeNonNull(this.errorConsumerHandler, handler ->
                    handler.accept(
                            new NullPointerException("Parent view not accessible to proceed")
                    ));
            return;
        }

        if (firstLoad && this.routeManager.isCurrentRoute(route)) {
            return;
        }

        this.routeManager.navigateTo(route);

        setView(route, onRoutesUpdate);
    }

    /**
     * Build the route as a Node view, in case not built just ignore it.
     */
    private void setView(IRoute route, BiFunction<Node, @NonNull Node, Transition> onRoutesUpdate) {
        this.validatePreviousTransition();

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

        transition.setOnFinished(evt -> {
            NullableUtils.executeNonNull(previousNode, childrenContainer::remove);
            this.cancelNavHolder.set(null);
        });

        // Set the values to cancel the view transition.
        this.cancelNavHolder.set(Pair.of(Arrays.asList(previousNode, currentNode), transition));
        log.info("Showing node view...");
        transition.play();
    }

    @Override
    public void navigateToHome() {
        this.navigateTo(EMainRoute.HOME, this.onNavigateBackTransition);
    }

    @Override
    public void navigateBack() {
        if (!this.routeManager.canNavigateBack()) return;

        EMainRoute previousRoute = this.routeManager.previousRoute();
        this.navigateTo(previousRoute, this.onNavigateBackTransition);
    }

    private void validatePreviousTransition() {
        Pair<List<Node>, Transition> holder = this.cancelNavHolder.get();

        if (Objects.isNull(holder)) return;

        var views = holder.getLeft();
        var transition = holder.getRight();

        // IF not running get back
        if (!transition.getStatus().equals(Animation.Status.RUNNING)) return;

        transition.stop();

        for (Node view : views) {
            NullableUtils.executeNonNull(view,
                    this.parentContainer.getChildren()::remove);
        }
    }
}
