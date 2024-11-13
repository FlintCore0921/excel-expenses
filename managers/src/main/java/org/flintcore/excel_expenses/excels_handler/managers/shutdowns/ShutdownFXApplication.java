package org.flintcore.excel_expenses.excels_handler.managers.shutdowns;

import data.utils.NullableUtils;
import jakarta.annotation.PostConstruct;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.excels_handler.managers.properties.CompoundResourceBundle;
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
        if (!Platform.isFxApplicationThread()) {
            log.warn("Only trigger close inside the FX Thread. Ensure you use it correctly.");
            return;
        }

        if (!this.closeTrigger.compareAndSet(false, true)) return;

        final Alert shutdownAlert = setupShutdownAlert();

        PauseTransition waitAfterClose = new PauseTransition(Duration.seconds(1.5));

        waitAfterClose.setOnFinished(event -> {
            shutdownAlert.close();
            Platform.exit();
        });

        if (Objects.isNull(subscriptions) || this.subscriptions.isEmpty()) {
            waitAfterClose.play();
            return;
        }

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
                waitAfterClose.play();
            }
        });
    }

    private Alert setupShutdownAlert() {
        final Alert shutDownAlert = new Alert(Alert.AlertType.NONE);
        triggerAlertOnScreen(shutDownAlert);
        return shutDownAlert;
    }

    private void triggerAlertOnScreen(Alert shutDownAlert) {
        if (Objects.isNull(stage)) {
            log.warn("Stage panel not provided!");
            return;
        }

        shutDownAlert.setHeaderText(bundles.getString("messages.closing_app"));
        shutDownAlert.setTitle(bundles.getString("messages.saving_resources"));

        shutDownAlert.setOnShown(e -> this.stage.hide());
        shutDownAlert.show();
    }
}
