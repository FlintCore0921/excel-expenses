package org.flintcore.utilities.susbcriptions;

import java.util.function.*;

public final class SubscriptionUtils {
    private SubscriptionUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static Consumer<String> consumeSubscribeMap(
            DoubleConsumer handler,
            ToDoubleFunction<String> mapper
    ) {
        return value -> handler.accept(mapper.applyAsDouble(value));
    }

    public static BiConsumer<String, String> consumeBiSubscribeMap(
            DoubleConsumer handler,
            ToDoubleFunction<String> mapper
    ) {
        return consumeBiSubscribeMap(handler, mapper, () -> 0.0);
    }

    public static BiConsumer<String, String> consumeBiSubscribeMap(
            DoubleConsumer handler
    ) {
        return consumeBiSubscribeMap(handler, Double::parseDouble, () -> 0.0);
    }

    public static BiConsumer<String, String> consumeBiSubscribeMap(
            DoubleConsumer handler,
            DoubleSupplier onEmpty
    ) {
        return consumeBiSubscribeMap(handler, Double::parseDouble, onEmpty);
    }

    public static BiConsumer<String, String> consumeBiSubscribeMap(
            DoubleConsumer handler,
            ToDoubleFunction<String> mapper,
            DoubleSupplier onEmpty
    ) {
        return (old, value) -> {
            double computedValue;
            try {
                computedValue = mapper.applyAsDouble(value);
            } catch (NumberFormatException e) {
                computedValue = onEmpty.getAsDouble();
            }
            handler.accept(computedValue);
        };
    }
}
