package org.flintcore.excel_expenses.excels_handler.services.details;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.flintcore.excelib.commons.utilities.LambdaUtils;
import org.flintcore.excelib.functions.workbooks.details.XSSFWorkBookDetailsBuilder;
import org.flintcore.excelib.models.xssf.XSSFWorkBookDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Log4j2
public class XSSFWorkBookDetailsService implements IXSSFDetailsService<XSSFWorkBookDetails, XSSFWorkbook> {

    public Future<XSSFWorkBookDetails> getDetailsOf(XSSFWorkbook workbook) {
        return CompletableFuture.supplyAsync(
                LambdaUtils.toSupplier(new XSSFWorkBookDetailsBuilder(), workbook)
        );
    }
}
