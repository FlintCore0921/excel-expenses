package org.flintcore.excel_expenses.controllers;

import org.flintcore.excel_expenses.factories.MainNavbarConfiguratorFactory;
import org.flintcore.excel_expenses.handlers.WindowActionsHolder;
import org.flintcore.excel_expenses.handlers.routers.IRoute;
import org.flintcore.excel_expenses.handlers.routers.main.EMainRoute;
import org.flintcore.excel_expenses.handlers.routers.routers.ApplicationRouter;
import org.flintcore.excel_expenses.models.NodeWrapper;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MainViewController implements Initializable {

    @FXML
    private Label topbarTitle;

    @FXML
    private ScrollPane bodyPane;

    @FXML
    private Circle btnClose;

    @FXML
    private Circle btnMinimize;

    private final ApplicationRouter applicationRouter;
    private final MainNavbarConfiguratorFactory navbarItemFactory;
    private final SidebarController sidebarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new WindowActionsHolder(btnClose, btnMinimize);

        setErrorHandlers();
        buildNavbarItems();

        Pane content = (Pane) this.bodyPane.getContent();
        this.applicationRouter.setParentContainer(content);
        Platform.runLater(this.sidebarController.getControllerOf(EMainRoute.HOME)::trigger);
    }

    private void setErrorHandlers() {
        // Set error handler
        this.applicationRouter.getErrorConsumerHandler()
                .addErrorConsumer(e -> {
                    if (e.getClass() != IOException.class) return;

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Unable to start application");
                    alert.showAndWait();

                    Platform.exit();
                });
    }

    private void buildNavbarItems() {
        Map<IRoute, NodeWrapper<Node, MenuItemController>> nodeWrapperList =
                navbarItemFactory.buildMainNavbarItems(
                        EMainRoute.availableRoutes(),
                        this.applicationRouter::navigateTo
                );


        this.sidebarController.setRoutes(nodeWrapperList);
    }

    private void navigateToRoute(EMainRoute route) {
        if (this.sidebarController.getControllerOf(route).isSelected()) return;
        this.applicationRouter.navigateTo(route);
    }

    private void navigateToRoute(EMainRoute route, Function<Node, Transition> transitionSupplier) {
        this.applicationRouter.navigateTo(route, ($, crr) -> transitionSupplier.apply(crr));
    }
}
