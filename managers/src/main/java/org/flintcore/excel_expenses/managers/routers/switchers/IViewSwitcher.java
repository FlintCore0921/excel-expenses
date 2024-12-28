package org.flintcore.excel_expenses.managers.routers.switchers;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.NonNull;

public interface IViewSwitcher {
    Transition switchTo(@NonNull Pane parent,
                        @NonNull Pair<Node, Duration> newView,
                        Pair<Node, Duration> oldView);
    Transition switchTo(@NonNull Pane parent, @NonNull Pair<Node, Duration> newView);
}
