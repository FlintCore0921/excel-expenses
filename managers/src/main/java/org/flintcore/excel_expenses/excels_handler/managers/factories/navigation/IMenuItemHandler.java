package org.flintcore.excel_expenses.excels_handler.managers.factories.navigation;

import javafx.event.ActionEvent;
import org.flintcore.excel_expenses.excels_handler.models.menues.SidebarItemStyle;

import java.util.function.Consumer;

public interface IMenuItemHandler {
    void setText(String t);

    void setOnTrigger(Consumer<ActionEvent> consumer);

    void addStyle(SidebarItemStyle style);

    void removeStyle(SidebarItemStyle style);

    boolean isSelected();

    void trigger();
}