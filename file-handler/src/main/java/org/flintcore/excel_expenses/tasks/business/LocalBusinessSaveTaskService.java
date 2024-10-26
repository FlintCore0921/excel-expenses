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
import lombok.Setter;
import org.flintcore.excel_expenses.files.business.LocalBusinessFileManager;
import org.flintcore.excel_expenses.models.expenses.LocalBusiness;
import org.flintcore.excel_expenses.models.lists.SerialListHolder;
import org.flintcore.excel_expenses.models.subscriptions.events.IEventSubscriptionHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class LocalBusinessSaveTaskService
        extends Service<Void>
        implements IEventSubscriptionHolder<WorkerStateEvent, Runnable> {

    private final LocalBusinessFileManager localBusinessFileManager;

    @Setter
    private Supplier<SerialListHolder<LocalBusiness>> localBusinessSupplier;

    private Map<EventType<WorkerStateEvent>, List<Runnable>> subscriptions;

    public Subscription addSubscription(EventType<WorkerStateEvent> type, Runnable action) {
        initSubscriptionsHolder();
        List<Runnable> subscriptionsIn = this.subscriptions
                .computeIfAbsent(type, __ -> Collections.synchronizedList(new ArrayList<>()));

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

    private void initSubscriptionsHolder() {
        NullableUtils.executeIsNull(this.subscriptions,
                () -> this.subscriptions = new ConcurrentHashMap<>());
    }

    @PostConstruct
    private void setupListeners() {
        EventHandler<WorkerStateEvent> subscriptionsHandler = callSubscriptionsHandler();
        setOnReady(subscriptionsHandler);
        setOnScheduled(subscriptionsHandler);
        setOnRunning(subscriptionsHandler);
        setOnSucceeded(subscriptionsHandler);
        setOnCancelled(subscriptionsHandler);
        setOnFailed(subscriptionsHandler);
    }

    private EventHandler<WorkerStateEvent> callSubscriptionsHandler() {
        return e -> NullableUtils.executeNonNull(
                this.subscriptions,
                subs -> NullableUtils.executeNonNull(
                        subs.get(e.getEventType()),
                        l -> l.iterator().forEachRemaining(Runnable::run)
                )
        );
    }

    @Override
    @PreDestroy
    public void close() {
        NullableUtils.executeNonNull(this.subscriptions, Map::clear);
    }
}
