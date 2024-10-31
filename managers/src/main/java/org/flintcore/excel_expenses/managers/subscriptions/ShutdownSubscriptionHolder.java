package org.flintcore.excel_expenses.managers.subscriptions;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to call when app or the manager owner is closed.
 */
@Component
@Log4j2
public final class ShutdownSubscriptionHolder implements ISubscriptionHolder<Object, Runnable> {
    private Map<Object, Set<Runnable>> subscriptions;
    private AtomicBoolean completed;

    @PostConstruct
    private void setAtomics() {
        this.completed = new AtomicBoolean(false);
    }

    @Override
    public Subscription addSubscription(Object type, Runnable action) {
        initSubscriptions();

        this.subscriptions.computeIfAbsent(type, this::buildSubscriptionHolder)
                .add(action);

        return () -> this.subscriptions.get(type).remove(action);
    }

    @Override
    public void addOneTimeSubscription(Object type, Runnable action) {
        Runnable onCall = () -> {
            action.run();
            this.subscriptions.get(type).remove(action);
        };

        this.addSubscription(type, onCall);
    }

    @Override
    public void addOneTimeSubscription(@NonNull List<Object> types, Runnable action) {
        if (types.isEmpty()) return;

        Runnable onCall = () -> {
            action.run();
            types.forEach(type -> this.subscriptions.get(type).remove(action));
        };

        ISubscriptionHolder.super.addOneTimeSubscription(types, onCall);
    }

    private void initSubscriptions() {
        if (Objects.isNull(subscriptions)) {
            synchronized (ShutdownSubscriptionHolder.class) {
                NullableUtils.executeIsNull(this.subscriptions,
                        () -> this.subscriptions = new ConcurrentHashMap<>()
                );
            }
        }
    }

    private Set<Runnable> buildSubscriptionHolder(Object __) {
        return new CopyOnWriteArraySet<>();
    }

    @Override
    @PreDestroy
    public void close() throws IOException {
        if (!this.completed.compareAndSet(false, true) &&
                Objects.isNull(subscriptions) || this.subscriptions.isEmpty()) return;

        int totalTasks = this.subscriptions.values().stream()
                .mapToInt(Collection::size)
                .sum();

        CountDownLatch counterWait = new CountDownLatch(totalTasks);
        ExecutorService tasksExecutor = Executors.newSingleThreadExecutor();

        for (Map.Entry<Object, Set<Runnable>> entry : this.subscriptions.entrySet()) {
            tasksExecutor.submit(() -> {
                try {
                    entry.getValue().forEach(Runnable::run);
                } catch (Exception e) {
                    log.error("Error with subscription in {}",
                            entry.getKey().getClass().getSimpleName());
                    log.error(e.getMessage(), e);
                } finally {
                    counterWait.countDown();
                }
            });
        }

        tasksExecutor.execute(() -> {
            try {
                counterWait.await();
            } catch (InterruptedException ignored) {
            } finally {
                tasksExecutor.shutdown();
            }
        });
    }
}
