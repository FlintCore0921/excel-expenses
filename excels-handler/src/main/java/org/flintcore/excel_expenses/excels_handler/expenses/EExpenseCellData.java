package org.flintcore.excel_expenses.excels_handler.expenses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellType;

@RequiredArgsConstructor
@Getter
public enum EExpenseCellData {

    DATE(CellType.STRING),
    LOCATION_NAME(CellType.STRING),
    RNC(CellType.STRING),
    NCF(CellType.STRING),
    NON_ITBIS_AMOUNT(CellType.NUMERIC),
    ITBIS_AMOUNT(CellType.NUMERIC),
    SERVICE_AMOUNT(CellType.NUMERIC),
    TOTAL_AMOUNT(CellType.NUMERIC),;

    public static final String[] COLUMN_HEADERS = {
            "Fecha", "NOMBRE DEL ESTABLECIMIENTO", "RNC",
            "NCF", "MONTO SIN ITBIS", "ITBIS",
            "% SERVICIO", "MONTO TOTAL"
    };

    private final CellType cellType;

    public String getTitleColumn() {
        return COLUMN_HEADERS[ordinal()];
    }
}
