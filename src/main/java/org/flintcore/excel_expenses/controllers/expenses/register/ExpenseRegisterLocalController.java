package org.flintcore.excel_expenses.controllers.expenses.register;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.FXController;
import org.flintcore.excel_expenses.managers.routers.expenses.IRegisterSectionController;
import org.flintcore.excel_expenses.models.business.IBusiness;

import java.net.URL;
import java.util.ResourceBundle;

@FXController
@Log4j2
@RequiredArgsConstructor
public class ExpenseRegisterLocalController implements IRegisterSectionController, Initializable {

    @FXML
    private ComboBox<IBusiness> localPicker;

    @FXML
    private VBox parentNode;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public Pane getNode() {
        return this.parentNode;
    }
}
