package org.flintcore.utilities.fx;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextInputControl;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class BindingsUtils {

    /**
     * Return to default bindings or null parameters in functions.
     */
    public static final BooleanBinding DEFAULT_BINDING = Bindings.createBooleanBinding(() -> false);

    private BindingsUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }


    public static BooleanBinding createAnyBlankBinding(@NonNull TextInputControl... textControls) {
        if (Objects.isNull(textControls)) return DEFAULT_BINDING;
        return createAnyBlankBinding(List.of(textControls));
    }

    public static BooleanBinding createAnyBlankBinding(List<TextInputControl> textControls) {
        StringProperty[] propertyList = textControls.stream()
                .map(TextInputControl::textProperty)
                .toArray(StringProperty[]::new);

        return Bindings.createBooleanBinding(
                () -> Arrays.stream(propertyList).map(Property::getValue)
                        .anyMatch(String::isBlank), propertyList
        );
    }
}
