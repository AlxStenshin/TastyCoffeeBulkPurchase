package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.ActiveSessionClosesSoonNotificationEvent;

public interface CustomerSessionClosesSoonNotificationPublisherService {
    void createAndPublishPaymentRequest(ActiveSessionClosesSoonNotificationEvent event);

}
