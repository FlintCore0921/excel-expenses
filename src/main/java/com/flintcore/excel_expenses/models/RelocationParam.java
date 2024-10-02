package com.flintcore.excel_expenses.models;

import javafx.scene.Node;
import javafx.stage.Window;

import java.awt.*;

public record RelocationParam(
        Dimension range,
        Window window
) {}
