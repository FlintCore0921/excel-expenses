package com.flintcore.excel_expenses.handlers.files;

public enum ESerializableExtensions {
    DAT, SER;

    public String suffixExtension() {
        return "." + this.name().toLowerCase();
    }

    public static ESerializableExtensions getDefault() {
        return values()[0];
    }
}
