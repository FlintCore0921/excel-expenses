package org.flintcore.utilities.animations;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.apache.commons.lang3.tuple.Pair;

public final class ViewAnimationUtils {
    private ViewAnimationUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Subscription animateTranslateBySubscription(
            BooleanBinding subscriber,
            Duration duration,
            Node node,
            Pair<Double, Double> translateXRange,
            Pair<Double, Double> translateYRange
    ) {
        TranslateTransition transition = new TranslateTransition(duration, node);

        return subscriber.subscribe(completed -> {
            transition.stop();

            transition.setFromX(extractBy(translateXRange, completed));
            transition.setFromY(extractBy(translateYRange, completed));
            transition.setToX(extractBy(translateXRange, !completed));
            transition.setToY(extractBy(translateYRange, !completed));

            transition.playFromStart();
        });
    }

    public static Subscription animateTranslateFadedBySubscription(
            BooleanBinding subscriber,
            Duration duration,
            Node node,
            Pair<Double, Double> translateXRange,
            Pair<Double, Double> translateYRange,
            Pair<Double, Double> fadeRange
    ) {
        TranslateTransition translateTransition = new TranslateTransition(duration, node);
        FadeTransition fadeTransition = new FadeTransition(duration, node);

        ParallelTransition joinedTransition = new ParallelTransition(
                translateTransition, fadeTransition
        );

        return subscriber.subscribe(isCompleted -> {
            joinedTransition.stop();

            translateTransition.setFromX(extractBy(translateXRange, isCompleted));
            translateTransition.setFromY(extractBy(translateYRange, isCompleted));

            translateTransition.setToX(extractBy(translateXRange, !isCompleted));
            translateTransition.setToY(extractBy(translateYRange, !isCompleted));

            fadeTransition.setFromValue(extractBy(fadeRange, isCompleted));
            fadeTransition.setToValue(extractBy(fadeRange, !isCompleted));

            joinedTransition.playFromStart();
        });
    }

    private static Double extractBy(Pair<Double, Double> range, boolean completed) {
        return completed ? range.getLeft() : range.getRight();
    }
}
