package com.flintcore.excel_expenses.models.locations;

import javafx.stage.Window;

import java.awt.*;

public record RelocationParam(
        Dimension range,
        Window window
) {}
