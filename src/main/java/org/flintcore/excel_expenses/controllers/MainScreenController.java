package org.flintcore.excel_expenses.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.routers.IApplicationRouter;
import org.flintcore.excel_expenses.managers.routers.routes.EMainRoute;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Log4j2
@RequiredArgsConstructor
public class MainScreenController implements Initializable {
    private final IApplicationRouter<EMainRoute> mainRouter;

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        // Init listeners
        // Init the screen that wraps all inner views
        mainRouter.setParentContainer(this.mainScreen);
        mainRouter.navigateToHome();

        // Init update resources.
    }

    @FXML
    private StackPane mainScreen;

    @FXML
    private Label mainTitle;

    @FXML
    private ScrollPane scrollPane;
}
