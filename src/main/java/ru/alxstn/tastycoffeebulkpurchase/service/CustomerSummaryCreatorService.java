package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

public interface CustomerSummaryCreatorService {
    String buildCustomerSummary(Customer customer, Session session);
}
