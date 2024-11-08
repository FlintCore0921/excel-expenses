package org.flintcore.excel_expenses.managers.filters;

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
    public final boolean filterByContains(String filterValue, Supplier<String>... filters) {
        return filterByContains(filterValue, Arrays.asList(filters));
    }

    public boolean filterByContains(String filterValue, List<Supplier<String>> filters) {
        return filters.stream().allMatch(value -> Objects.equals(value.get(), filterValue));
    }
}
