package org.flintcore.excel_expenses.excels_handler.tasks;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.flintcore.excel_expenses.excels_handler.expenses.EExpenseCellData;
import org.flintcore.excel_expenses.excels_handler.expenses.ExpenseCellValueMapper;
import org.flintcore.excel_expenses.excels_handler.models.receipts.Receipt;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

@Log4j2
public class LoadExpenseDataOnTableTask<T extends Receipt>
        implements BiFunction<XSSFTable, List<? extends T>, Boolean> {

    public static final String[] EXPENSE_COLUMN_HEADERS = EExpenseCellData.COLUMN_HEADERS;

    @Override
    public Boolean apply(final XSSFTable table, @NonNull List<? extends T> rows) {
        // Get second start row (headers omitted)
        XSSFSheet sheet = table.getXSSFSheet();

        int startCellRow = table.getStartRowIndex(),
                endCellRow = table.getEndRowIndex();

        Pair<Boolean, Integer> nextRowPair = findNextRow(sheet, startCellRow, endCellRow);

        if(!nextRowPair.getKey()) {
            table.setDataRowCount(table.getTotalsRowCount() + rows.size());
        }

        startCellRow = nextRowPair.getValue();

        // Iterate data and make it rows.
        for (T expense : rows) {
            appendInRow(sheet, startCellRow++, expense);
        }


        return Boolean.TRUE;
    }

    private Pair<Boolean, Integer> findNextRow(XSSFSheet sheet, final int startCellRow, int endCellRow) {
        int validRow = startCellRow;

        do {
            XSSFRow row = sheet.getRow(validRow);

            // If row not created or does not have any cell.
            // row cell value is empty.
            if (row == null || row.getLastCellNum() == 0
                    || Objects.isNull(row.getCell(0))
                    || StringUtils.isBlank(row.getCell(0).getRawValue())
            ) return Pair.of(true, validRow);

            validRow++;
        } while (validRow < endCellRow);

        return Pair.of(false, validRow);
    }

    private void appendInRow(XSSFSheet sheet, int cellRow, T expense) {
        XSSFRow row = ObjectUtils.getIfNull(
                sheet.getRow(cellRow),
                () -> sheet.createRow(cellRow)
        );

        for (int columnIndex = 0; columnIndex < EXPENSE_COLUMN_HEADERS.length; columnIndex++) {
            try {
                applyDataOnCell(row, columnIndex, expense);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    private void applyDataOnCell(XSSFRow row, int columnIndex, T expense) throws IllegalStateException {
        final ExpenseCellValueMapper cellValueMapper = new ExpenseCellValueMapper();
        Pair<EExpenseCellData, ?> cellDataPair = cellValueMapper.getCellValueOf(columnIndex, expense);

        CellType cellType = cellDataPair.getKey().getCellType();
        XSSFCell rowCell = row.createCell(columnIndex, cellType);

        switch (cellType) {
            case NUMERIC -> rowCell.setCellValue((double) cellDataPair.getValue());
            case STRING -> rowCell.setCellValue(cellDataPair.getValue().toString());
            case BLANK -> rowCell.setCellValue("");
            default -> log.warn("Invalid type to set: {}", cellDataPair);
        }
    }
}
