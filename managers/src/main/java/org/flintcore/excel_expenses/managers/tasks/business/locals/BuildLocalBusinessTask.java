package org.flintcore.excel_expenses.managers.tasks.business.locals;

import lombok.extern.log4j.Log4j2;
import org.flintcore.excel_expenses.managers.validators.LocalBusinessValidator;
import org.flintcore.excel_expenses.models.business.LocalBusiness;

import java.util.function.Supplier;
import java.util.stream.Stream;

@Log4j2
public class BuildLocalBusinessTask implements Supplier<LocalBusiness> {

    private final LocalBusinessValidator localBusinessValidator;

    private final Supplier<String> localNameSupplier;
    private final Supplier<String> localRNCSupplier;

    public BuildLocalBusinessTask(
            LocalBusinessValidator localBusinessValidator,
            Supplier<String> localNameSupplier,
            Supplier<String> localRNCSupplier) {
        this.localBusinessValidator = localBusinessValidator;
        this.localNameSupplier = localNameSupplier;
        this.localRNCSupplier = localRNCSupplier;
    }

    public BuildLocalBusinessTask(Supplier<String> localNameSupplier, Supplier<String> localRNCSupplier) {
        this(new LocalBusinessValidator(), localRNCSupplier, localNameSupplier);
    }

    @Override
    public LocalBusiness get() {
        LocalBusiness localBusiness;
        String localName = localNameSupplier.get(),
                localRNC = localRNCSupplier.get();

        validateProvidedValues(localName, localRNC);

        localBusiness = LocalBusiness.builder()
                .name(localName)
                .RNC(localRNC)
                .build();

        validateBusinessContent(localBusiness);

        return localBusiness;
    }

    private void validateProvidedValues(String localName, String localRNC) {
        if (Stream.of(localName, localRNC).anyMatch(String::isBlank)) {
            log.error("Error at moment to check fields for local business.");
            throw new NullPointerException("Fields required for local business.");
        }
    }

    private void validateBusinessContent(LocalBusiness localBusiness) {
        if (!localBusinessValidator.validateContent(localBusiness)) {
            log.error("Error at moment to validate data for building local business.");
            throw new IllegalArgumentException("Invalid format of the data from Local business.");
        }
    }
}
