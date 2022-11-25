package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;

public interface CustomerPaymentRequestPublisherService {
    void createAndPublishSummary(PurchaseSummaryNotificationEvent event);
}
