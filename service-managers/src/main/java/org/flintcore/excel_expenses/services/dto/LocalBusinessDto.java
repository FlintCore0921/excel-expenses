package org.flintcore.excel_expenses.services.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public record LocalBusinessDto(
        @JsonAlias({"rnc", "RNC"})
        String rnc,
        String name,
        Status status

) implements Serializable, Comparable<LocalBusinessDto> {
    @Serial
    private static final long serialVersionUID = 7721L;

    @Override
    public int compareTo(@NonNull LocalBusinessDto o) {
        return Comparator.comparing(LocalBusinessDto::rnc)
                .thenComparing(LocalBusinessDto::status)
                .compare(this, o);
    }

    enum Status {
        ACTIVE, SUSPENDED, DOWN;
    }
}
