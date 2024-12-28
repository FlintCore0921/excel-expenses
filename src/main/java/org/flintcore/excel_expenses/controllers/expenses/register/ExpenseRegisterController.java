package org.flintcore.excel_expenses.controllers.expenses.register;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.FXController;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.routers.expenses.RegisterSectionRouter;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

@FXController
@Log4j2
@RequiredArgsConstructor
public class ExpenseRegisterController implements Initializable {
    private final CompoundResourceBundle resources;
    private final RegisterSectionRouter registerSectionRouter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Set navigation
        registerSectionRouter.setParentPane(sectionHolder);
        registerSectionRouter.navigateToHome();
    }

    @FXML
    private ImageView btnNext;

    @FXML
    private ImageView btnPrevious;

    @FXML
    private StackPane sectionHolder;

    @FXML
    private Label section_title;
}
