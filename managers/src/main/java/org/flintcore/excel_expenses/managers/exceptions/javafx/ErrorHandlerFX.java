package org.flintcore.excel_expenses.managers.exceptions.javafx;


import data.utils.NullableUtils;
import javafx.scene.control.Alert;

public class ErrorHandlerFX {
    private ErrorHandlerFX(){}

    private static class SingletonHelper {
        private static final ErrorHandlerFX INSTANCE = new ErrorHandlerFX();
    }

    public static ErrorHandlerFX getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * @param title can be nullable
     * @param contentMessage can be nullable
     * @param onClose can be nullable
     * */
    public void errorAlert(String title, String contentMessage, Runnable onClose){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        NullableUtils.executeNonNull(title, () -> alert.setTitle(title));
        NullableUtils.executeNonNull(contentMessage, () -> alert.setContentText(contentMessage));
        alert.showAndWait();

        NullableUtils.executeNonNull(onClose);
    }
}
