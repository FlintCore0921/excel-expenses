package org.flintcore.excel_expenses.controllers;

import org.flintcore.excel_expenses.models.menues.SidebarItemStyle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Getter;
import org.flintcore.excel_expenses.managers.factories.navigation.IMenuItemHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Getter
@Component
@Scope("prototype")
public class MenuItemController implements IMenuItemHandler {
    @FXML
    private Button btnTrigger;

    public void setOnTrigger(Consumer<ActionEvent> event) {
        btnTrigger.setOnAction(event::accept);
    }

    public void setText(String text) {
        btnTrigger.setText(text);
    }

    public void addStyle(SidebarItemStyle style) {
        btnTrigger.getStyleClass().add(style.identifier);
    }

    public void removeStyle(SidebarItemStyle style) {
        btnTrigger.getStyleClass().remove(style.identifier);
    }

    public boolean isSelected() {
        return btnTrigger.getStyleClass().contains(SidebarItemStyle.ITEM_SELECTED.identifier);
    }

    public void trigger() {
        this.btnTrigger.fire();
    }
}
