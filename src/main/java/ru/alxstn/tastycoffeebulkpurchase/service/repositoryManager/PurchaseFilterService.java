package ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.DiscardedProductProperties;

import java.util.Map;

public interface PurchaseFilterService {
    Map<Product, Integer> filterPurchases(DiscardedProductProperties requiredTypes, Map<Product, Integer> allPurchases);
}
