package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.*;

import java.util.List;

public interface SessionPurchaseReportCreatorService {
    List<PurchaseEntry> createPerProductReport(Session session);
    List<CustomerPurchaseEntry> createPerCustomerReport(Session session);

}
