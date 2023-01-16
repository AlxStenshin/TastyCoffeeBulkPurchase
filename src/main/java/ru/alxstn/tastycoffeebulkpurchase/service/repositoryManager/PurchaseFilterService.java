package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductProperties;

import java.util.Map;

public interface PurchaseFilterService {
    Map<Product, Integer> filterPurchases(RequiredProductProperties requiredTypes, Map<Product, Integer> allPurchases);
}
