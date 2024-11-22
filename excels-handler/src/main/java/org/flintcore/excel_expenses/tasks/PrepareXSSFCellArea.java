package org.flintcore.excel_expenses.tasks;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import java.util.Objects;
import java.util.function.BiFunction;

public class PrepareXSSFCellArea implements BiFunction<Sheet, AreaReference, Sheet> {
    @Override
    public Sheet apply(Sheet sheet, final AreaReference area) {
        CellReference lastCell = area.getLastCell();

        var lastRowInd = lastCell.getRow();
        var lastColumnInd = lastCell.getCol();

        for (int rowIndex = 0; rowIndex < lastRowInd; rowIndex++) {
            var row = createRow(sheet, rowIndex);

            for (int columnIndex = 0; columnIndex <= lastColumnInd; columnIndex++) {
                createColumnCell(row, columnIndex);
            }
        }

        return sheet;
    }

    private Row createRow(Sheet sheet, final int rowIndex) {
        return ObjectUtils.getIfNull(
                sheet.getRow(rowIndex),
                () -> sheet.createRow(rowIndex)
        );
    }

    private void createColumnCell(Row row, final int columnIndex) {
        if (Objects.isNull(row.getCell(columnIndex)))
            row.createCell(columnIndex);
    }
}
