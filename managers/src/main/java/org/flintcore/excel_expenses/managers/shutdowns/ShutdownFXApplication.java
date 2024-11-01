package org.flintcore.excel_expenses.managers.shutdowns;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Subscriptions previously trigger before FX Application called close.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public final class ShutdownFXApplication implements IShutdownHandler<Runnable> {
    private final CompoundResourceBundle bundles;
    @Setter
    private Stage stage;
    private Map<Object, Set<Runnable>> subscriptions;
    private AtomicBoolean closeTrigger;

    @PostConstruct
    private void setAtomics() {
        this.closeTrigger = new AtomicBoolean(false);
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

        IShutdownHandler.super.addOneTimeSubscription(types, onCall);
    }

    private void initSubscriptions() {
        if (Objects.isNull(subscriptions)) {
            synchronized (ShutdownFXApplication.class) {
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
    public void close() {
        if (!this.closeTrigger.compareAndSet(false, true) ||
                Objects.isNull(subscriptions) || this.subscriptions.isEmpty()) return;

        this.setupAlert();

        int totalTasks = this.subscriptions.values().stream()
                .mapToInt(Collection::size)
                .sum();

        CountDownLatch counterWait = new CountDownLatch(totalTasks);
        ExecutorService tasksExecutor = Executors.newSingleThreadExecutor();

        for (Map.Entry<Object, Set<Runnable>> entry : this.subscriptions.entrySet()) {
            tasksExecutor.execute(() -> {
                for (Runnable runnable : entry.getValue()) {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        log.error("Error with subscription in {}",
                                entry.getKey().getClass().getSimpleName());
                        log.error(e.getMessage(), e);
                    } finally {
                        counterWait.countDown();
                    }
                }
            });
        }

        tasksExecutor.execute(() -> {
            try {
                counterWait.await();
            } catch (InterruptedException ignored) {
            } finally {
                tasksExecutor.shutdown();
                Platform.exit();
            }
        });
    }

    private void setupAlert() {
        Runnable runnable = triggerAlertOnScreen();

        if (!Platform.isFxApplicationThread()) Platform.runLater(runnable);
        else runnable.run();
    }

    private Runnable triggerAlertOnScreen() {
        return () -> {
            if (Objects.isNull(stage)) {
                log.warn("Stage panel not provided!");
                return;
            }

            Alert closeAlert = new Alert(Alert.AlertType.INFORMATION);
            closeAlert.setTitle(bundles.getString("messages.saving_resources"));
            closeAlert.setHeaderText(bundles.getString("messages.closing_app"));

            closeAlert.setOnShown(e -> this.stage.hide());
            closeAlert.show();
        };
    }
}
