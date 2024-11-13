package org.flintcore.excel_expenses.excels_handler.services.builders;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excel_expenses.excels_handler.resources.IPathResource;
import org.flintcore.excel_expenses.excels_handler.resources.PathResource;
import org.flintcore.excel_expenses.excels_handler.utils.files.FileCreator;
import org.flintcore.excel_expenses.excels_handler.utils.paths.PathValidator;
import org.flintcore.excelib.commons.executors.DefaultThreadPoolHolder;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

// Use ContextConfiguration for submodules test.
@SpringBootTest(classes = {
        XSSFWorkbookCreatorService.class,
        XSSFFileService.class,
        FileCreator.class,
        PathValidator.class,
        PathResource.class,
        DefaultThreadPoolHolder.class
})
@ActiveProfiles({"test"})
class ExcelFileManagementServiceTest {
    @Autowired
    private XSSFWorkbookCreatorService XSSFCreatorService;
    @Autowired
    private IPathResource pathResource;

    @Test
    void BuildFileByExcel() {
        assertDoesNotThrow(() -> {
            validatePathResourceApplied();

            String path = pathResource.getExternalExpensePath();
            XSSFWorkbook workbook = XSSFCreatorService.loadWorkBook(path).get();

            assertNotNull(workbook);

            // ??
            System.out.printf("Names: %s%n", workbook.getAllNames());

            int numberOfSheets = workbook.getNumberOfSheets();
            System.out.printf("No. Sheet: %s%n", numberOfSheets);

            for (int i = 0; i < numberOfSheets; i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                System.out.printf("Sheet #%d name: %s%n", i + 1, sheet.getSheetName());
                List<XSSFTable> tables = sheet.getTables();

                var tableNameList = tables.stream().map(XSSFTable::getName).toList();
                System.out.printf("\tNo. Tables: %d%n\t%s%n", tables.size(), tableNameList);
            }
        });
    }

    @Test
    void checksIfUpdateDataAtRuntime() {
        assertDoesNotThrow(() -> {
            validatePathResourceApplied();

            String path = pathResource.getExternalExpensePath();
            XSSFWorkbook workbook = XSSFCreatorService.loadWorkBook(path).get();

            assertNotNull(workbook);

            // ??
            System.out.printf("Names: %s%n", workbook.getAllNames());

            int numberOfSheets = workbook.getNumberOfSheets();

            assumeTrue(numberOfSheets > 0, "Requires at least one sheet");

            XSSFSheet sheet = workbook.getSheetAt(0);

            var firstTableFound = assertDoesNotThrow(
                    () -> sheet.getTables().stream().findFirst()
                    , "Could not found any table in first sheet.");

            firstTableFound.ifPresentOrElse(table -> {
                CellReference startCell = table.getStartCellReference();
                CellReference endCell = table.getEndCellReference();

                System.out.printf("Sheet table printed: %s%n", table.getName());

                for (int rowIdx = startCell.getRow(); rowIdx <= endCell.getRow(); rowIdx++) {
                    Row row = sheet.getRow(rowIdx);
                    if (row != null) {
                        for (int colIdx = startCell.getCol(); colIdx <= endCell.getCol(); colIdx++) {
                            Cell cell = row.getCell(colIdx);

                            if (cell != null) {
                                // Print cell value based on its type
                                switch (cell.getCellType()) {
                                    case STRING:
                                        System.out.print(cell.getStringCellValue() + "\t");
                                        break;
                                    case NUMERIC:
                                        System.out.print(cell.getNumericCellValue() + "\t");
                                        break;
                                    case BOOLEAN:
                                        System.out.print(cell.getBooleanCellValue() + "\t");
                                        break;
                                    case FORMULA:
                                        var cellEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                                        var cellValue = cellEvaluator.evaluate(cell);
                                        System.out.print(cellValue + "\t");
                                        break;
                                    default:
                                        System.out.print(" \t");
                                }
                            }
                        }
                        System.out.println();
                    }
                }
            }, () -> Assertions.fail("Could not get table from sheet."));

        });
    }

    private void validatePathResourceApplied() {
        assumeTrue(
                Objects.nonNull(pathResource),
                "Path resource need an instance"
        );
    }
}