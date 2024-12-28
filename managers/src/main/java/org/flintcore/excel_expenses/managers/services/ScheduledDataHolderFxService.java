package org.flintcore.excel_expenses.managers.services;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;

import java.util.Objects;

/**
 * Class to holds the list ob data to send.
 */
public abstract class ScheduledDataHolderFxService<T> extends ScheduledFxService<T> {
    protected ObservableSet<T> dataSetList;

    public ScheduledDataHolderFxService(
            GeneralEventSubscriptionHandler eventHandler,
            ShutdownFXApplication shutDownHolder) {
        super(eventHandler, shutDownHolder);
    }

    protected void initObservableList() {
        if (Objects.nonNull(this.dataSetList)) return;

        if (!Platform.isFxApplicationThread())
            Platform.runLater(this::initFields);
        else this.initFields();

        setupOnLoadListeners();
        setupOnReadListeners();
    }

    @SuppressWarnings("unchecked")
    private void initFields() {
        this.dataSetList = FXCollections.observableSet();
    }
}
