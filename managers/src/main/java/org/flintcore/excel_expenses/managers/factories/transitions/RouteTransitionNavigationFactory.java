package org.flintcore.excel_expenses.managers.factories.transitions;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Component
@Lazy
public class RouteTransitionNavigationFactory extends TransitionFactory {

    private BiFunction<@NonNull Node, @NonNull Duration, Transition> switchEnterTransition;
    private BiFunction<@NonNull Node, @NonNull Duration, Transition> switchExitTransition;

    public BiFunction<@NonNull Node, @NonNull Duration, Transition> getSwitchEnterTransition() {
        if (Objects.isNull(switchEnterTransition))
            this.switchEnterTransition = (node, duration) -> this.createParallelTransition(
                    node, duration, List.of(this::createSlideInHorizontallyTransition,
                            this::createFadeInTransition
                    )
            );

        return switchEnterTransition;
    }

    public BiFunction<@NonNull Node, @NonNull Duration, Transition> getSwitchExitTransition() {
        if (Objects.isNull(switchExitTransition))
            this.switchExitTransition = this::createFadeOutTransition;

        return switchExitTransition;
    }
}
