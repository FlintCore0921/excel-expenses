package com.flintcore.excel_expenses.handlers.routers.routers;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.function.BiConsumer;

public interface IApplicationRouter<I extends IRoute> extends IRouter<I> {
    void setParentContainer(Pane pane);

    void navigateTo(IRoute route);

    void navigateTo(IRoute route, BiConsumer<Node, Node> onRoutesUpdate);
}
