package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;

public interface NewProductAnalyzerService {
    void analyzeNewProducts(List<Product> newProducts);
}
