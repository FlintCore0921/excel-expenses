package org.flintcore.excel_expenses.managers.events.combo_box.keyboards;

import data.utils.NullableUtils;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Subscription;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.flintcore.excel_expenses.models.subscriptions.consumers.ISubscriptionConsumerHandler;
import org.flintcore.excel_expenses.models.subscriptions.consumers.MultiSubscriptionConsumerHandler;

/**
 * Up and Down Movement
 */
@NoArgsConstructor
public class RemoteKeyListener implements EventHandler<KeyEvent> {
    private ISubscriptionConsumerHandler<KeyCode, Runnable> onHandleListeners;

    @Override
    public void handle(KeyEvent evt) {
        this.onHandleListeners.accept(evt.getCode());
    }

    public Subscription appendHandlerListener(@NonNull KeyCode key, final Runnable listener) {
        initHandlerListeners();
        return this.onHandleListeners.addSubscription(key, listener);
    }

    public Subscription appendGeneralListener(final Runnable listener) {
        initHandlerListeners();
        return this.onHandleListeners.addGeneralSubscription(listener);
    }

    public Subscription appendLastListener(@NonNull KeyCode key, final Runnable listener) {
        initHandlerListeners();
        return this.onHandleListeners.addLastSubscription(key, listener);
    }

    private void initHandlerListeners() {
        NullableUtils.executeIsNull(this.onHandleListeners,
                () -> this.onHandleListeners = new MultiSubscriptionConsumerHandler<>()
        );
    }
}
