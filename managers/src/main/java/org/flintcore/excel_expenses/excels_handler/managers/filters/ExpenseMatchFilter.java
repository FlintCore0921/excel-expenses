package org.flintcore.excel_expenses.excels_handler.managers.filters;

import lombok.NonNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Component
@Scope("prototype")
public final class ExpenseMatchFilter {
    @SafeVarargs
    public final boolean filterByContains(String filterValue, Supplier<@NonNull String>... filters) {
        return filterByContains(filterValue, Arrays.asList(filters));
    }

    public boolean filterByContains(String filterValue, List<Supplier<@NonNull String>> filters) {
        return filters.stream().anyMatch(value -> value.get().contains(filterValue));
    }

    public boolean filterByEquals(String filterValue, List<Supplier<@NonNull String>> filters) {
        return filters.stream().anyMatch(value -> Objects.equals(value.get(), filterValue));
    }
}
