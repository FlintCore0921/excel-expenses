package org.flintcore.excel_expenses.managers.rules;

public interface IReceiptRules {
    int NFC_CODE_LENGTH = 11;

    static String getCodePattern() {
        return "\\w\\d{%d}".formatted(NFC_CODE_LENGTH -1);
    }
    static String getCodeFormatterPattern() {
        return "\\w?\\d{0,%d}".formatted(NFC_CODE_LENGTH -1);
    }
}
