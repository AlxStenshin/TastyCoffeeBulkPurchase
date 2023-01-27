package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;

import java.util.Map;

public interface PurchaseFilterService {
    Map<Product, Integer> filterPurchases(SessionProductFilters requiredTypes, Map<Product, Integer> allPurchases);
    SessionProductFilters createFilter(Session session, SessionProductFilterType filterType, boolean targetState);
}
