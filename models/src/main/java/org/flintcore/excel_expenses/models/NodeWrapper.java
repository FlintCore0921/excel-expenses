package org.flintcore.excel_expenses.models;

import javafx.scene.Node;

public record NodeWrapper<T extends Node, C>(
        T nodeView,
        C controller
) {}
