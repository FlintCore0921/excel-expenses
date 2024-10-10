package org.flintcore.excel_expenses.factories.transitions;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.util.Duration;
import lombok.NonNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Component
public class TransitionFactory {
    public Transition createSlideInHorizontallyTransition(
            Node node,
            Duration duration,
            Supplier<Double> fromValue
    ) {
        Bounds bounds = ObjectUtils.defaultIfNull(
                node.getParent().getBoundsInParent(), node.getBoundsInParent()
        );

        Supplier<Double> doubleSupplier = ObjectUtils
                .defaultIfNull(fromValue, IDefaultTransitions.SLIDE_HORIZONTAL_FROM);

        double fromXValue = bounds.getWidth() * doubleSupplier.get();

        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(fromXValue);
        transition.setToX(0.0);
        return transition;
    }

    public Transition createSlideOutHorizontallyTransition(
            Node node,
            Duration duration
    ) {
        return createSlideOutHorizontallyTransition(node, duration, IDefaultTransitions.SLIDE_HORIZONTAL_TO);
    }

    public Transition createSlideInHorizontallyTransition(
            Node node,
            Duration duration
    ) {
        return createSlideInHorizontallyTransition(node, duration, IDefaultTransitions.SLIDE_HORIZONTAL_FROM);
    }

    public Transition createSlideOutHorizontallyTransition(
            Node node,
            Duration duration,
            Supplier<Double> fromValue
    ) {
        Bounds bounds = ObjectUtils.defaultIfNull(
                node.getParent().getBoundsInParent(), node.getBoundsInParent()
        );

        Supplier<Double> doubleSupplier = ObjectUtils
                .defaultIfNull(fromValue, IDefaultTransitions.SLIDE_HORIZONTAL_FROM);

        double fromXValue = bounds.getWidth() * doubleSupplier.get();

        TranslateTransition transition = new TranslateTransition(duration, node);
        transition.setFromX(0.0);
        transition.setToX(fromXValue);
        return transition;
    }

    public Transition createFadeInTransition(
            Node node,
            Duration duration
    ) {
        FadeTransition transition = new FadeTransition(duration, node);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        return transition;
    }

    public Transition createFadeOutTransition(
            Node node,
            Duration duration
    ) {
        FadeTransition transition = new FadeTransition(duration, node);
        transition.setFromValue(0.0);
        transition.setToValue(1.0);
        return transition;
    }

    /**
     * Creates a SequentialTransition from a list of transitions mapped from a provided function.
     *
     * @param node        the node to be animated
     * @param duration    the duration for each transition
     * @param transitions a list of transition mappers
     * @return a SequentialTransition that contains all the transitions
     */
    public Transition createSequentialTransition(
            Node node,
            Duration duration,
            List<BiFunction<@NonNull Node, @NonNull Duration, Transition>> transitions
    ) {
        SequentialTransition sequentialTransition = new SequentialTransition();

        transitions.stream()
                .map(mapper -> mapper.apply(node, duration))
                .forEach(sequentialTransition.getChildren()::add);

        return sequentialTransition;
    }

    /**
     * Creates a SequentialTransition from a list of transitions mapped from a provided function.
     *
     * @param node        the node to be animated
     * @param duration    the duration for each transition
     * @param transitions a list of transition mappers
     * @return a SequentialTransition that contains all the transitions
     */
    public Transition createParallelTransition(
            Node node,
            Duration duration,
            List<BiFunction<@NonNull Node, @NonNull Duration, Transition>> transitions
    ) {
        ParallelTransition transition = new ParallelTransition();

        transitions.stream()
                .map(mapper -> mapper.apply(node, duration))
                .filter(Objects::nonNull)
                .forEach(transition.getChildren()::add);

        return transition;
    }


}
