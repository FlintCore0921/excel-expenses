package org.flintcore.excel_expenses.excels_handler.models.files;

public enum EFileExtension {
    /** XSSF File. */
    XLSX;

    public String asDotExtension() {
        return ".%s".formatted(this.name().toLowerCase());
    }
}
