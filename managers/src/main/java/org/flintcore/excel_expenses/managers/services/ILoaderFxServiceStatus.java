package org.flintcore.excel_expenses.managers.services;

import javafx.beans.property.ReadOnlyBooleanProperty;

public interface ILoaderFxServiceStatus {
    ReadOnlyBooleanProperty isRequestingProperty();
}
