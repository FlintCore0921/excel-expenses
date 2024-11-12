package org.flintcore.excel_expenses.services.details;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.flintcore.excelib.commons.utilities.LambdaUtils;
import org.flintcore.excelib.functions.sheets.details.XSSFSheetDetailsBuilder;
import org.flintcore.excelib.models.xssf.XSSFSheetDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Log4j2
public class XSSFSheetDetailsService implements IXSSFDetailsService<XSSFSheetDetails, XSSFSheet>{

    public Future<XSSFSheetDetails> getDetailsOf(XSSFSheet workbook) {
        return CompletableFuture.supplyAsync(
                LambdaUtils.toSupplier(new XSSFSheetDetailsBuilder(), workbook)
        );
    }
}
