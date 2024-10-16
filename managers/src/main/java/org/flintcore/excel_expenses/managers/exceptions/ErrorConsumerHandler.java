package org.flintcore.excel_expenses.managers.exceptions;

import data.utils.NullableUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@Scope("prototype")
public class ErrorConsumerHandler implements Consumer<Exception> {
    @Override
    public void accept(Exception e) {
        this.errorConsumers.forEach(errorConsumer -> errorConsumer.accept(e));
    }

    private List<Consumer<Exception>> errorConsumers;

    public void addErrorConsumer(final Consumer<Exception> consumer) {
        NullableUtils.executeIsNull(this.errorConsumers, () ->this.errorConsumers = new ArrayList<>());
        errorConsumers.add(consumer);
    }

    public void removeErrorConsumer(final Consumer<Exception> consumer) {
        NullableUtils.executeNonNull(this.errorConsumers, errorConsumers -> errorConsumers.remove(consumer));
    }
}
