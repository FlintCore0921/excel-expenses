package org.flintcore.excel_expenses.services.details;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.flintcore.excelib.commons.utilities.LambdaUtils;
import org.flintcore.excelib.functions.tables.details.XSSFTableDetailsBuilder;
import org.flintcore.excelib.models.xssf.XSSFTableDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Log4j2
public class XSSFTableDetailsService implements IXSSFDetailsService<XSSFTableDetails, XSSFTable> {

    public Future<XSSFTableDetails> getDetailsOf(XSSFTable workbook) {
        return CompletableFuture.supplyAsync(
                LambdaUtils.toSupplier(new XSSFTableDetailsBuilder(), workbook)
        );
    }
}
