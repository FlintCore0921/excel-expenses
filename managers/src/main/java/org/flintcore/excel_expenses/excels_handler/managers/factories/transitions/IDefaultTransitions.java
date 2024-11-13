package org.flintcore.excel_expenses.excels_handler.managers.factories.transitions;

import java.util.function.Supplier;

public interface IDefaultTransitions {
    /**
     * Multiplier based on width
     */
    Supplier<Double> SLIDE_HORIZONTAL_FROM = () -> -1.0;
    Supplier<Double> SLIDE_HORIZONTAL_TO = () -> 1.0;
}
