package org.flintcore.utilities.events;

import jakarta.annotation.Nonnull;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.NonNull;


public final class KeyEventUtils {
    private KeyEventUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @NonNull
    public static KeyEvent buildEventFromCode(@Nonnull KeyCode code) {
        return new KeyEvent(KeyEvent.ANY, code.getChar(), code.getName(), code,
                false, false, false, false);
    }
}
