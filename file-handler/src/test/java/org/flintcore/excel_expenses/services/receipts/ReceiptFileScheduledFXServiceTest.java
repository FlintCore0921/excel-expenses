package org.flintcore.excel_expenses.services.receipts;

import org.flintcore.excel_expenses.managers.shutdowns.ShutdownFXApplication;
import org.flintcore.excel_expenses.managers.subscriptions.SubscriptionHolder;
import org.flintcore.excel_expenses.managers.timers.ApplicationScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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