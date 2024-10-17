package org.flintcore.excel_expenses.managers.timers;

import data.utils.NullableUtils;
import jakarta.annotation.PreDestroy;
import javafx.util.Subscription;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ApplicationScheduler implements IScheduler {
    private Timer holderTimer;

    @Override
    public Subscription schedule(@NonNull TimerTask task, Duration delay, Duration period) {
        return this.schedule(task, delay.toMillis(), period.toMillis());
    }

    @Override
    public Subscription schedule(@NonNull TimerTask task, Duration period) {
        return this.schedule(task, 0L, period.toMillis());
    }

    @Override
    public Subscription schedule(@NonNull TimerTask task, long delay, long period) {
        initScheduler();
        this.holderTimer.schedule(task, delay, period);

        return task::cancel;
    }

    @Override
    public Subscription schedule(@NonNull TimerTask task, long period) {
        return this.schedule(task, 0L, period);
    }

    private void initScheduler() {
        NullableUtils.executeIsNull(this.holderTimer, () -> this.holderTimer = new Timer());
    }


    @PreDestroy
    private void cancelTimers() {
        NullableUtils.executeNonNull(this.holderTimer, Timer::cancel);
    }
}
