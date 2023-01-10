package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosedNotificationEvent;

public interface CustomerPaymentRequestPublisherService {
    void createAndPublishPaymentRequest(ActiveSessionClosedNotificationEvent event);
}
