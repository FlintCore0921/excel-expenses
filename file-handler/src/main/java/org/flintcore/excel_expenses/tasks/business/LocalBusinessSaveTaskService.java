package org.flintcore.excel_expenses.tasks.business;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableScheduledService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Supplier;

@Component
@Log4j2
@RequiredArgsConstructor
public class LocalBusinessSaveTaskService extends ObservableScheduledService<Void> {

    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    @Setter
    private Supplier<SerialListHolder<LocalBusiness>> localBusinessSupplier;


    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.getEventListenerHolder().get(type).remove(action);
        });
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                SerialListHolder<LocalBusiness> data = localBusinessSupplier.get();
                localBusinessFileManager.updateDataSet(data);
                return null;
            }
        };
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
