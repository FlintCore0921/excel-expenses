package org.flintcore.excel_expenses.handlers.fields;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

import java.util.function.Consumer;
import java.util.function.Function;

public class NumericTextFieldChangeListener<T extends Number> implements ChangeListener<String> {
    private PauseTransition handler;
    private final Consumer<T> consumer;
    private final Function<String, T> mapper;

    public NumericTextFieldChangeListener(Consumer<T> consumer, Function<String, T> mapper) {
        this.consumer = consumer;
        this.mapper = mapper;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        NullableUtils.executeIsNull(this.handler, this::setHandler);
        this.handler.setOnFinished(evt -> consumer.accept(mapper.apply(newValue)));
        this.handler.playFromStart();
    }

    private void setHandler() {
        this.handler = new PauseTransition(Duration.millis(700));
    }
}
