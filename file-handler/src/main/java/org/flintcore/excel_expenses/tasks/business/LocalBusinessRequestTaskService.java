package org.flintcore.excel_expenses.tasks.business;

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
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.files.business.LocalBusinessFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
@Log4j2
public class LocalBusinessRequestTaskService
        extends Service<List<LocalBusiness>>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    private final LocalBusinessFileManager localBusinessFileManager;
    private Map<EventType<WorkerStateEvent>, Set<Runnable>> subscriptions;

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        initSubscriptionsHolder();

        Set<Runnable> subscriptionsIn = this.subscriptions
                .computeIfAbsent(type, __ -> new CopyOnWriteArraySet<>());
        subscriptionsIn.add(action);

        return () -> subscriptionsIn.remove(action);
    }

    @Override
    public void addOneTimeSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        this.addSubscription(type, () -> {
            action.run();
            this.subscriptions.get(type).remove(action);
        });
    }

    private void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new ConcurrentHashMap<>()
        );
    }

    @PostConstruct
    private void setupListeners() {
        EventHandler<WorkerStateEvent> subscriptionsHandler = callSubscriptionsHandler();

        this.setOnScheduled(subscriptionsHandler);
        this.setOnReady(subscriptionsHandler);
        this.setOnRunning(subscriptionsHandler);
        this.setOnSucceeded(subscriptionsHandler);
        this.setOnFailed(subscriptionsHandler);
        this.setOnCancelled(subscriptionsHandler);
    }

    private EventHandler<WorkerStateEvent> callSubscriptionsHandler() {
        return e -> NullableUtils.executeNonNull(this.subscriptions,
                subs -> NullableUtils.executeNonNull(subs.get(e.getEventType()),
                        l -> l.iterator().forEachRemaining(Runnable::run)
                )
        );
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
        NullableUtils.executeNonNull(this.subscriptions, Map::clear);
    }
}
