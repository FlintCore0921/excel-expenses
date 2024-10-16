package org.flintcore.excel_expenses.tasks;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.files.business.LocalBusinessFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.subscriptions.IEventSubscriptionHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocalBusinessRequestTaskService
        extends Service<List<LocalBusiness>>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    private final LocalBusinessFileManager localBusinessFileManager;
    private Map<EventType<WorkerStateEvent>, List<Runnable>> subscriptions;

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        initSubscriptionsHolder();

        List<Runnable> subscriptionsIn = this.subscriptions
                .computeIfAbsent(type, __ -> new ArrayList<>());
        subscriptionsIn.add(action);

        return () -> subscriptionsIn.remove(action);
    }

    private void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new HashMap<>());
    }

    @PostConstruct
    private void setupListeners() {
        EventHandler<WorkerStateEvent> subscriptionsHandler = callSubscriptionsHandler();

        setOnSucceeded(subscriptionsHandler);
        setOnFailed(subscriptionsHandler);
        setOnScheduled(subscriptionsHandler);
        setOnCancelled(subscriptionsHandler);
        setOnReady(subscriptionsHandler);
    }

    private EventHandler<WorkerStateEvent> callSubscriptionsHandler() {
        return e -> NullableUtils.executeNonNull(this.subscriptions,
                subs -> NullableUtils.executeNonNull(subs.get(e.getEventType()),
                        l -> l.forEach(Runnable::run)
                )
        );
    }

    @Override
    protected Task<List<LocalBusiness>> createTask() {
        return new Task<>() {
            @Override
            protected List<LocalBusiness> call() throws Exception {
                return List.copyOf(localBusinessFileManager.getDataList());
            }
        };
    }

    @PreDestroy
    private void onDestroy() {
        NullableUtils.executeNonNull(subscriptions, Map::clear);
    }
}
