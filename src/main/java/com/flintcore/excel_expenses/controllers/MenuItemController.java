package com.flintcore.excel_expenses.controllers;

import com.flintcore.excel_expenses.models.menues.SidebarItemStyle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Getter
@Component
public class MenuItemController {
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

    public boolean isSelected(){
        return btnTrigger.getStyleClass().contains(SidebarItemStyle.ITEM_SELECTED.identifier);
    }
}
