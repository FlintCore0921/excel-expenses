package org.flintcore.excel_expenses.files.extensions;

public enum ESerializableExtension {
    DAT, SER;

    public String suffixExtension() {
        return "." + this.name().toLowerCase();
    }

    public static ESerializableExtension getDefault() {
        return DAT;
    }
}
