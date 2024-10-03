package com.flintcore.excel_expenses.controllers;

import com.flintcore.excel_expenses.factories.MainNavbarConfiguratorFactory;
import com.flintcore.excel_expenses.handlers.WindowActionsHolder;
import com.flintcore.excel_expenses.handlers.routers.main.EMainRoute;
import com.flintcore.excel_expenses.handlers.routers.main.MainViewRouter;
import com.flintcore.excel_expenses.models.NodeWrapper;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
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

    private StackPane bodyPaneContent;

    private final MainViewRouter router;
    private final MainNavbarConfiguratorFactory navbarItemFactory;
    private final SidebarController sidebarController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new WindowActionsHolder(btnClose, btnMinimize);

        setErrorHandlers();
        buildNavbarItems();

        bodyPaneContent = (StackPane) this.bodyPane.getContent();
// Just testing
        navigateToRoute(EMainRoute.Home, node -> {
            node.opacityProperty().set(0);

            FadeTransition transition = new FadeTransition(Duration.millis(600), node);
            transition.setFromValue(0D);
            transition.setToValue(1D);
            return transition;
        });
    }

    private void setErrorHandlers() {
        // Set error handler
        this.router.getErrorConsumerHandler()
                .addErrorConsumer(e -> {
                    if (e.getClass() != IOException.class) return;

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Unable to start application");
                    alert.showAndWait();

                    Platform.exit();
                });
    }

    private void buildNavbarItems() {
        Map<EMainRoute, NodeWrapper<Node, MenuItemController>> nodeWrapperList =
                navbarItemFactory.buildMainNavbarItems(
                        EMainRoute.availableRoutes(),
                        this::navigateToRoute
                );

        this.sidebarController.setRoutes(nodeWrapperList);
    }

    private void navigateToRoute(EMainRoute route) {
        if(this.sidebarController.getControllerOf(route).isSelected()) return;

        this.router.navigateTo(route, bodyPaneContent.getChildren()::add);
    }

    private void navigateToRoute(EMainRoute route, Function<Node, Transition> transitionSupplier) {
        this.router.navigateTo(route, bodyPaneContent.getChildren()::add, transitionSupplier);
    }
}
