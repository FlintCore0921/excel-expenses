package com.flintcore.excel_expenses.views.utils;

import com.flintcore.excel_expenses.factories.transitions.TransitionFactory;
import jakarta.annotation.PostConstruct;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Getter
@Component
@Lazy
public class RouteTransitionNavigationFactory extends TransitionFactory {

    private BiFunction<@NonNull Node, @NonNull Duration, Transition> switchEnterTransition;
    private BiFunction<@NonNull Node, @NonNull Duration, Transition> switchExitTransition;

    @PostConstruct
    public void setTransitions() {
        this.switchEnterTransition = (node, duration) -> this.createParallelTransition(
                node, duration,
                List.of(
                        this::createSlideInHorizontallyTransition,
                        this::createFadeOutTransition
                )
        );

        this.switchExitTransition = this::createFadeOutTransition;
    }
}
