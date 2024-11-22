package org.flintcore.excel_expenses.controllers;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.flintcore.excel_expenses.managers.factories.views.IItemViewHandler;
import org.flintcore.excel_expenses.models.expenses.IBusiness;
import org.flintcore.excel_expenses.models.receipts.Receipt;
import org.flintcore.utilities.dates.DateUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class ExpenseItemController implements IItemViewHandler<Receipt, VBox>, Initializable {

    private final ObjectProperty<Receipt> receiptProperty;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupListeners();
    }

    private void setupListeners() {
        setupReceiptListener();
    }

    private void setupReceiptListener() {
        /* listen the value in the business name when change*/
        this.localNameHolder.textProperty().bind(
                this.receiptProperty.map(Receipt::business)
                        .map(IBusiness::getName)
        );

        /* listen the value in the receipt date when change*/
        this.dateHolder.textProperty().bind(
                this.receiptProperty.map(Receipt::dateCreation)
                        .map(DateUtils.createPatternOf("dd 'of' MM"))
        );

        /* listen the value in the receipt date when change*/
        this.dateHolder.textProperty().bind(
                this.receiptProperty.map(Receipt::dateCreation)
                        .map(DateUtils.createPatternOf("dd 'of' MM"))
        );

        /* listen the value in the receipt total price when change*/
        this.priceHolder.textProperty().bind(
                this.receiptProperty.map(receipt -> receipt.getTotalPrice().toString())
        );
    }

    public ExpenseItemController() {
        this.receiptProperty = new SimpleObjectProperty<>();
    }

    @Override
    public void setValue(Receipt value) {
        this.receiptProperty.set(value);
    }

    @Override
    public Receipt getValue() {
        return this.receiptProperty.getValue();
    }

    @Override
    public VBox getView() {
        return this.parentPane;
    }

    @Override
    public void setOnEdit(Consumer<Receipt> action) {
        this.btnEdit.setOnMouseClicked(event -> action.accept(this.getValue()));
    }

    @Override
    public void setOnRemove(Consumer<Receipt> action) {
        this.btnDelete.setOnMouseClicked(event -> action.accept(this.getValue()));
    }

    @FXML
    private Circle btnDelete;

    @FXML
    private Circle btnEdit;

    @FXML
    private Label dateHolder;

    @FXML
    private Label localNameHolder;

    @FXML
    private HBox optionsBox;

    @FXML
    private VBox parentPane;

    @FXML
    private Label priceHolder;
}
