package org.flintcore.utilities.bindings;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;

import java.util.Objects;
// TODO Check this case more carefully

/**
 * Listen observable to trigger true if value not change, otherwise false.
 */
public class NoChangeObjectObservable<T> implements ObservableBooleanValue {
    protected final ObservableValue<T> observable;
    protected BooleanProperty value;
    protected T previousValue;

    public static <T> NoChangeObjectObservable<T> bind(ObservableValue<T> observable) {
        return new NoChangeObjectObservable<>(observable);
    }

    private NoChangeObjectObservable(ObservableValue<T> observable) {
        this.observable = observable;
        this.previousValue = observable.getValue();

        this.value = new SimpleBooleanProperty(true);

        // Listen on observable changes.
        addBindListener();
    }

    protected void addBindListener() {
        this.observable.addListener(
                (__o, old, curr) -> this.value.set(Objects.equals(previousValue, curr))
        );
    }

    @Override
    public boolean get() {
        try {
            return this.value.get();
        } finally {
            this.value.set(true);
        }
    }

    @Override
    public Boolean getValue() {
        return get();
    }

    @Override
    public void addListener(ChangeListener<? super Boolean> listener) {
        this.value.addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super Boolean> listener) {
        this.value.addListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        this.value.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        this.value.addListener(listener);
    }
}
