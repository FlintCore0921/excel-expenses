package org.flintcore.excel_expenses.configurations.receipts;

import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.events.GeneralEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.subscriptions.events.IOnceEventSubscriptionHandler;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReceiptFileFXServiceTest {
    @Mock
    private ReceiptFileFXService service;

    @InjectMocks
    ReceiptSaveTaskService receiptSaveTaskService;
    @InjectMocks
    ReceiptRequestTaskService requestTaskService;
    @InjectMocks
    GeneralEventSubscriptionHandler subscriptionManager;
    @InjectMocks
    ApplicationScheduler appScheduler;
    @InjectMocks
    ShutdownFXApplication shutDownSubscriptionHolder;

    @Test
    void createNewExpenseRegister() {
        assertNotNull(service);
    }
}