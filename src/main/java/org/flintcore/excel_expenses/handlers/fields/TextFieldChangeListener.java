package org.flintcore.excel_expenses.handlers.fields;

import data.utils.NullableUtils;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

import java.util.function.Consumer;

public class TextFieldChangeListener implements ChangeListener<String> {
    private PauseTransition handler;
    private final Consumer<String> consumer;

    public TextFieldChangeListener(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        NullableUtils.executeIsNull(this.handler, this::setHandler);
        this.handler.stop();
        this.handler.setOnFinished(evt -> consumer.accept(newValue));
        this.handler.playFromStart();
    }

    private void setHandler() {
        this.handler = new PauseTransition(Duration.millis(700));
    }
}
