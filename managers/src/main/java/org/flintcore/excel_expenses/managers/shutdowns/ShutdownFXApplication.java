package org.flintcore.excel_expenses.managers.shutdowns;

import jakarta.annotation.PostConstruct;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.properties.CompoundResourceBundle;
import org.flintcore.excel_expenses.managers.subscriptions.handlers.IOnceKeyLessSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.handlers.OnceKeyLessRunnableSubscriptionHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Subscriptions previously trigger before FX Application called close.
 */
@Component
@Log4j2
public final class ShutdownFXApplication implements IShutdownHandler {
    // SECONDS
    private final long AWAIT_TIME_S = 15L;

    private final CompoundResourceBundle bundles;
    // Make Holder
    private final IOnceKeyLessSubscriptionHandler<Runnable> shutdownHolder;

    @Setter
    private Stage stage;

    private AtomicBoolean closeTrigger;

    public ShutdownFXApplication(
            CompoundResourceBundle bundles,
            OnceKeyLessRunnableSubscriptionHandler shutdownHolder) {
        this.bundles = bundles;
        this.shutdownHolder = shutdownHolder;
    }

    @PostConstruct
    private void setAtomics() {
        this.closeTrigger = new AtomicBoolean(false);
    }

    @Override
    public Subscription handle(@NonNull Runnable value) {
        return this.shutdownHolder.handleOnce(value);
    }

    @Override
    public Subscription handle(@NonNull List<Runnable> values) {
        return this.shutdownHolder.handleOnce(values);
    }

    @Override
    public void run() {

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


        ExecutorService tasksExecutor = Executors.newSingleThreadExecutor();

        tasksExecutor.execute(this.shutdownHolder);

        tasksExecutor.shutdown();

        try {
            // If it does not end in this time waits.
            if (!tasksExecutor.awaitTermination(AWAIT_TIME_S, TimeUnit.SECONDS)) {
                tasksExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            tasksExecutor.shutdownNow();
        }

        waitAfterClose.play();
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
