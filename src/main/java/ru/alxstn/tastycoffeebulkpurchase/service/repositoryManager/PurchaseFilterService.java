package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;

import java.util.Map;

public interface PurchaseFilterService {
    Map<Product, Integer> filterPurchases(SessionProductFilters requiredTypes, Map<Product, Integer> allPurchases);
}
