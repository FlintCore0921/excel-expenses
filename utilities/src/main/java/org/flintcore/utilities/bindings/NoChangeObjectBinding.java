package org.flintcore.utilities.bindings;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;

import java.util.Objects;
// TODO Check this case more carefully
/**Listen observable to trigger true if value not change, otherwise false.*/
public class NoChangeObjectBinding<T> extends BooleanBinding {
    private final ObservableValue<T> observable;
    private T previousValue;

    private NoChangeObjectBinding(ObservableValue<T> observable) {
        this.observable = observable;
        this.previousValue = observable.getValue();

        // Listen on observable changes.
        this.observable.addListener((__o, old, curr) -> invalidate());
    }

    public static <T> NoChangeObjectBinding<T> bind(ObservableValue<T> observable) {
        return new NoChangeObjectBinding<>(observable);
    }

    @Override
    protected boolean computeValue() {
        try {
            return Objects.equals(previousValue, observable.getValue());
        } finally {
            this.previousValue = observable.getValue();
        }
    }
}
