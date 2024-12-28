package org.flintcore.excel_expenses.managers.exceptions.handlers;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.flintcore.excel_expenses.managers.routers.MainApplicationRouter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Profile("dev")
@AllArgsConstructor
public class ErrorConsoleHandler implements Consumer<Exception> {

    private MainApplicationRouter mainApplicationRouter;

    @PostConstruct
    public void listenRouters() {
        mainApplicationRouter.getErrorConsumerHandler()
                .addErrorConsumer(this);
    }

    @Override
    public void accept(Exception e) {
        System.out.println(e.getMessage());
    }
}
