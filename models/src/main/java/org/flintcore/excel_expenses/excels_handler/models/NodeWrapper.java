package org.flintcore.excel_expenses.excels_handler.models;

import javafx.scene.Node;

public record NodeWrapper<T extends Node, C>(
        T nodeView,
        C controller
) {}
