package org.flintcore.excel_expenses.handlers.routers.routers;

import org.flintcore.excel_expenses.handlers.routers.IRoute;
import org.flintcore.excel_expenses.handlers.routers.main.EMainRoute;
import javafx.animation.Transition;
import javafx.scene.Node;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IExternalRouter<I extends IRoute> extends IRouter<I>{

    void navigateTo(I route, Consumer<Node> paneConsumer, Function<Node, Transition> transition);

    public void navigateTo(final EMainRoute route, Consumer<Node> paneConsume);

    void navigateBack(Consumer<Node> paneConsumer, Function<Node, Transition> transition);
}
