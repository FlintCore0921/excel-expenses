package org.flintcore.excel_expenses.tasks.business;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessSerializeFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.subscriptions.tasks.ObservableFXService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Log4j2
public class LocalBusinessRequestTaskService extends ObservableFXService<List<LocalBusiness>> {

    private final LocalBusinessSerializeFileManager localBusinessFileManager;

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {

        Set<Runnable> subscriptionsIn = this.getEventListenerHolder()
                .computeIfAbsent(type, this::buildSubscriptionHolder);
        subscriptionsIn.add(action);

        return () -> subscriptionsIn.remove(action);
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.getEventListenerHolder().get(type).remove(action);
        });
    }

    @Override
    protected Task<List<LocalBusiness>> createTask() {
        return new Task<>() {
            @Override
            protected List<LocalBusiness> call() /*throws Exception*/ {
                return localBusinessFileManager.getDataList();
            }
        };
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.events, Map::clear);
    }
}
