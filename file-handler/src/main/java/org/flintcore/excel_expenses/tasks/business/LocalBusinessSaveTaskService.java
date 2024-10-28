package org.flintcore.excel_expenses.tasks.business;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class LocalBusinessSaveTaskService extends ObservableService<Void> {

    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    @Setter
    private Supplier<SerialListHolder<LocalBusiness>> localBusinessSupplier;


    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.events.get(type).remove(action);
        });
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                localBusinessFileManager.updateDataSet(
                        localBusinessSupplier.get()
                );
                return null;
            }
        };
    }

    @Override
    @PostConstruct
    protected void setupListeners() {
        super.setupListeners();
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
