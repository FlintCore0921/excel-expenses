package org.flintcore.excel_expenses.excels_handler.models.locations;

import javafx.stage.Window;

import java.awt.*;

public record RelocationParam(
        Dimension range,
        Window window
) {}
