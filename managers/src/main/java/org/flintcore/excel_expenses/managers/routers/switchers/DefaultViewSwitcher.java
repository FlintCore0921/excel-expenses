package org.flintcore.excel_expenses.managers.routers.switchers;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.factories.transitions.RouteTransitionNavigationFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

@Component
@Primary
@Scope("prototype")
@Log4j2
public class DefaultViewSwitcher implements IViewSwitcher {

    private Supplier<Transition> values;
    private final RouteTransitionNavigationFactory transitionFactory;

    public DefaultViewSwitcher(RouteTransitionNavigationFactory transitionFactory) {
        this.transitionFactory = transitionFactory;
    }

    @Override
    public Transition switchTo(
            @NonNull Pane parent,
            @NonNull Pair<Node, Duration> newView,
            Pair<Node, Duration> oldView) {

        ParallelTransition combineTransition = new ParallelTransition(
                this.switchTo(parent, newView)
        );

        if(Objects.nonNull(oldView) && Objects.nonNull(oldView.getKey())){
            combineTransition.getChildren().add(
                    this.transitionFactory.getSwitchExitTransition()
                            .apply(oldView.getKey(), oldView.getValue())
            );
        }

        return combineTransition;
    }

    @Override
    public Transition switchTo(@NonNull Pane parent, @NonNull Pair<Node, Duration> newView) {
        Node newViewNode = newView.getKey();

        if(!parent.getChildren().contains(newViewNode))
            parent.getChildren().add(newViewNode);

        newViewNode.opacityProperty().set(0D);

        return this.transitionFactory.getSwitchEnterTransition()
                .apply(newView.getKey(), newView.getValue());
    }
}
