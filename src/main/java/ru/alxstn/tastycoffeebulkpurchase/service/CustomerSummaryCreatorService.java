package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;

public interface CustomerSummaryCreatorService {
    String buildCustomerSummary(Customer customer);
}
