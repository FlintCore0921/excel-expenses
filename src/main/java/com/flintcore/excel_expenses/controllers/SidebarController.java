package com.flintcore.excel_expenses.controllers;

import com.flintcore.excel_expenses.handlers.routers.IRoute;
import com.flintcore.excel_expenses.handlers.routers.routers.ApplicationRouter;
import com.flintcore.excel_expenses.models.NodeWrapper;
import com.flintcore.excel_expenses.models.menues.SidebarItemStyle;
import data.utils.NullableUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class SidebarController implements Initializable {
    @FXML
    private VBox navbarActions;

    private final ApplicationRouter applicationRouter;

    private IRoute lastItemSelected;
    private ObservableMap<IRoute, MenuItemController> navbarController;

    public SidebarController(ApplicationRouter applicationRouter) {
        this.applicationRouter = applicationRouter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setRoutes(Map<IRoute, NodeWrapper<Node, MenuItemController>> routes) {
        NullableUtils.executeIsNull(this.navbarController,
                () -> this.navbarController = FXCollections.observableHashMap()
        );

        clearSidebar();

        routes.forEach(this::appendRoute);
    }

    public MenuItemController getControllerOf(IRoute key) {
        return this.navbarController.get(key);
    }

    public void appendRoute(IRoute identifier, NodeWrapper<Node, MenuItemController> node) {
        this.navbarActions.getChildren().add(node.nodeView());
        MenuItemController controller = node.controller();
        this.navbarController.put(identifier, controller);

        controller.setOnTrigger(ev -> {
            SidebarItemStyle selectedStyle = SidebarItemStyle.ITEM_SELECTED;
            NullableUtils.executeNonNull(lastItemSelected,
                    it -> this.getControllerOf(it).removeStyle(selectedStyle)
            );
            controller.addStyle(selectedStyle);
            lastItemSelected = identifier;

            this.applicationRouter.navigateTo(identifier);
        });
    }

    public void clearSidebar() {
        this.navbarController.clear();
        lastItemSelected = null;
    }
}
