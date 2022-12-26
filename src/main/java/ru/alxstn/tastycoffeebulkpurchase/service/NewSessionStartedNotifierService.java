package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

public interface NewSessionStartedNotifierService {
    void notifySubscribedCustomers(Session session);
}
