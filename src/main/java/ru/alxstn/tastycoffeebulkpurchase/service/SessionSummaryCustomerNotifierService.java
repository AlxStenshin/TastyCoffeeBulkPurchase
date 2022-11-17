package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;

public interface SessionSummaryCustomerNotifierService {
    void createAndPublishSummary(PurchaseSummaryNotificationEvent event);
}
