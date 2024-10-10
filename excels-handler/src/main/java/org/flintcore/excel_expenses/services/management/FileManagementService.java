package org.flintcore.excel_expenses.services.management;

import lombok.AllArgsConstructor;
import org.flintcore.excelib.services.builders.XSSFFileService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileManagementService {
    private final XSSFFileService workbookService;
}
