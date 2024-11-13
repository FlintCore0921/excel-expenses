package org.flintcore.excel_expenses.excels_handler.services.receipts;

import org.flintcore.excel_expenses.excels_handler.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.excels_handler.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.excels_handler.managers.timers.ApplicationScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReceiptFileScheduledFXServiceTest {
    @Mock
    private ReceiptFileScheduledFXService service;

    @InjectMocks
    ReceiptSaveTaskService receiptSaveTaskService;
    @InjectMocks
    ReceiptRequestTaskService requestTaskService;
    @InjectMocks
    SubscriptionHolder subscriptionManager;
    @InjectMocks
    ApplicationScheduler appScheduler;
    @InjectMocks
    ShutdownFXApplication shutDownSubscriptionHolder;

    @Test
    void createNewExpenseRegister() {
        assertNotNull(service);
    }
}