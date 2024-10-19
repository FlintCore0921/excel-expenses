package org.flintcore.excel_expenses.managers.events.combo_box.keyboards;

import data.utils.NullableUtils;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Subscription;
import org.flintcore.excel_expenses.models.subscriptions.consumers.ISubscriptionConsumerHandler;
import org.flintcore.excel_expenses.models.subscriptions.consumers.MultiSubscriptionConsumerHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Up and Down Movement
 */
public class ComboBoxRemoteKeyListener implements EventHandler<KeyEvent> {
    private ISubscriptionConsumerHandler<KeyCode, Runnable> onHandleListeners;

    private final Supplier<Integer> sizeSupplier;
    private int selectedIndex;

    public ComboBoxRemoteKeyListener(Supplier<Integer> sizeSupplier) {
        this.sizeSupplier = sizeSupplier;
        resetIndex();
    }


    @Override
    public void handle(KeyEvent evt) {
        this.onHandleListeners.accept(evt.getCode());
    }

    public Subscription appendOnEnterListener(final Consumer<Integer> listener) {
        initConsumerHandler();
        return this.onHandleListeners.addSubscription(KeyCode.ENTER,
                () -> listener.accept(selectedIndex)
        );
    }

    public Subscription appendOnHandleListener(KeyCode key, final Runnable listener) {
        initConsumerHandler();
        return () -> this.onHandleListeners.addSubscription(key, listener);
    }

    private void initConsumerHandler() {
        NullableUtils.executeIsNull(this.onHandleListeners,
                () -> this.onHandleListeners = new MultiSubscriptionConsumerHandler<>()
        );
    }

    private void onDownCalled() {
        this.selectedIndex = Math.min(this.sizeSupplier.get(), ++selectedIndex);
    }

    private void onUpCalled() {
        this.selectedIndex = Math.max(0, --selectedIndex);
    }

    // Reset the selection index of the ComboBox
    private void resetIndex() {
        selectedIndex = -1;
    }
}
