package org.flintcore.excel_expenses.models.receipts;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LocalNFCCode {
    E("E31"), B("B01");

    /**Size of the prefix*/
    public static final int FULL_SIZE = 3;

    @NonNull public final String fullPrefix;
}
