package org.flintcore.excel_expenses.excels_handler.resources;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

public final class XSSFUtils {
    private XSSFUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static void handleEmptyWorkbook(ThrowableConsumer<XSSFWorkbook> consumer) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            consumer.accept(workbook);
        }
    }

    public static void handleEmptyWorkbook(FileInputStream ips, ThrowableConsumer<XSSFWorkbook> consumer) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(ips)) {
            consumer.accept(workbook);
        }
    }

    @FunctionalInterface
    public interface ThrowableConsumer<T> {
        void accept(T t) throws Exception;
    }
}
