package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;

public interface CustomerSummaryCreatorService {
    String buildCustomerSummary(Customer customer, Session session) throws SessionNotFoundException;
}
