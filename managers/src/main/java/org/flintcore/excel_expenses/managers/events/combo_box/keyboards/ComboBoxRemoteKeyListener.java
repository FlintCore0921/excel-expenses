package org.flintcore.excel_expenses.managers.events.combo_box.keyboards;

import data.utils.NullableUtils;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Subscription;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Up and Down Movement
 */
@RequiredArgsConstructor
public class ComboBoxRemoteKeyListener implements EventHandler<KeyEvent> {
    private final Supplier<Integer> sizeSupplier;
    private int selectedIndex = -1;
    private List<Runnable> onHandleListeners;
    private List<Consumer<Integer>> onEnterListeners;

    public Subscription appendOnEnterListener(final Consumer<Integer> listener) {
        NullableUtils.executeIsNull(this.onEnterListeners,
                () -> this.onEnterListeners = new ArrayList<>());

        if (!this.onEnterListeners.contains(listener)) {
            this.onEnterListeners.add(listener);
        }

        return () -> this.onEnterListeners.remove(listener);
    }

    public Subscription appendOnHandleListener(final Runnable listener) {
        NullableUtils.executeIsNull(this.onHandleListeners,
                () -> this.onHandleListeners = new ArrayList<>());

        if (!this.onHandleListeners.contains(listener)) {
            this.onHandleListeners.add(listener);
        }

        return () -> this.onHandleListeners.remove(listener);
    }

    @Override
    public void handle(KeyEvent evt) {
        KeyCode code = evt.getCode();

        switch (code) {
            case UP -> onUpCalled();
            case DOWN -> onDownCalled();
            case ENTER -> onEnterCalled();
        }
    }

    private void onKeyHandledSelected() {
        NullableUtils.executeNonNull(this.onHandleListeners,
                l -> List.copyOf(l).forEach(Runnable::run));
    }

    private void onEnterCalled() {
        NullableUtils.executeNonNull(this.onEnterListeners,
                l -> {
                    List.copyOf(l).forEach(c -> c.accept(this.selectedIndex - 1));
                    this.selectedIndex = -1;
                }
        );
    }

    private void onDownCalled() {
        this.selectedIndex = Math.min(this.sizeSupplier.get(), ++selectedIndex);
        onKeyHandledSelected();
    }


    private void onUpCalled() {
        this.selectedIndex = Math.max(0, --selectedIndex);
        onKeyHandledSelected();
    }
}
