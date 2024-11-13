package org.flintcore.excel_expenses.excels_handler.resources;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class PathResource implements IPathResource {
    @Override
    public String getExternalExpensePath() {
        return "C:\\Users\\Elior\\Desktop\\FORMULARIO DE EXPENSE XPERTCODE- Julio - 2024 - Elio Erick Ramos Mosquea.xlsm";
    }
}
