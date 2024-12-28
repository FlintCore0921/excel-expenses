package org.flintcore.excel_expenses.managers.routers;

import lombok.NonNull;

public interface IRouter<R> {
    void navigateBack();

    void navigateToHome();

    void navigateTo(@NonNull R route);
}
