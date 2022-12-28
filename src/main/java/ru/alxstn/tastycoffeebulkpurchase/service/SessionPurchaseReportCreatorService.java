package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.*;

import java.util.Map;

public interface SessionPurchaseReportCreatorService {
    Map<Product, Integer> createPerProductReport(Session session);
    // ToDo:
    // List<CustomerPurchaseEntry> createPerCustomerReport(Session session);

}
