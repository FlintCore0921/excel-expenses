package org.flintcore.excel_expenses.excels_handler.managers.timers;

import javafx.util.Subscription;

import java.time.Duration;
import java.util.TimerTask;

public interface IScheduler {
    Subscription schedule(TimerTask task, long delay, long period);
    Subscription schedule(TimerTask task, Duration delay, Duration period);
    Subscription schedule(TimerTask task, long period);
    Subscription schedule(TimerTask task, Duration period);
}
