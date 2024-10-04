package com.flintcore.excel_expenses.handlers.routers.routers;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface IApplicationRouter<I extends IRoute> extends IRouter<I> {
    void setParentContainer(Pane pane);

    void navigateTo(IRoute route);

    void navigateTo(IRoute route, BiFunction<Node, Node, Transition> onRoutesUpdate);
}
