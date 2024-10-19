package org.flintcore.excel_expenses.managers.events.businesses;

import data.utils.NullableUtils;
import javafx.util.Subscription;
import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.models.expenses.IBusiness;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Log4j2
public class BusinessSelectionHandler<T extends IBusiness> implements Consumer<T> {
    private List<Consumer<T>> consumers;

    @Override
    public void accept(T business) {
        log.debug(business);

        NullableUtils.executeNonNull(
                this.consumers,
                l -> List.copyOf(l).forEach(call -> call.accept(business))
        );
    }

    public Subscription appendConsumer(Consumer<T> consumer) {
        NullableUtils.executeIsNull(this.consumers,
                () -> this.consumers = new ArrayList<>());
        this.consumers.add(consumer);

        return () -> this.consumers.remove(consumer);
    }
}
