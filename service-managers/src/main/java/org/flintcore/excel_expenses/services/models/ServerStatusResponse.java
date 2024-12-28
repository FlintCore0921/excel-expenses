package org.flintcore.excel_expenses.services.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record ServerStatusResponse(
        @JsonAlias("READY") boolean status
) {}
