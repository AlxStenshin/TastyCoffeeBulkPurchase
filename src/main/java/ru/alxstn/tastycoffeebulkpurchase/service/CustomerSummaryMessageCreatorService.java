package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;

public interface CustomerSummaryMessageCreatorService {
    String buildCustomerSummaryMessage(Session session, Customer customer) throws SessionNotFoundException;
}
