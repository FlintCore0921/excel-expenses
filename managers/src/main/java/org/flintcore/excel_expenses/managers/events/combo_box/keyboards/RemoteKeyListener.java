package org.flintcore.excel_expenses.managers.events.combo_box.keyboards;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Subscription;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.flintcore.excel_expenses.managers.subscriptions.handlers.GeneralDynamicRunnableSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.handlers.IKeyLessSubscriptionHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Up and Down Movement
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class RemoteKeyListener implements EventHandler<KeyEvent> {
    @Lazy
    private final GeneralDynamicRunnableSubscriptionHandler<KeyCode> eventHandler;
    @Lazy
    private final IKeyLessSubscriptionHandler<Runnable> keylessHandler;

    @Override
    public void handle(KeyEvent evt) {
        this.eventHandler.accept(evt.getCode());
        this.keylessHandler.run();
    }

    public Subscription appendListener(@NonNull KeyCode key, final Runnable listener) {
        return this.eventHandler.handle(key, listener);
    }

    public Subscription appendListenerOnce(@NonNull KeyCode key, final Runnable listener) {
        return this.eventHandler.handleOnce(key, listener);
    }

    public Subscription appendListener(final Runnable handler) {
        return this.keylessHandler.handle(handler);
    }
}
