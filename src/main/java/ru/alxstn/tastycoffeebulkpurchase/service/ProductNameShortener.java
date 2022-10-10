package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public interface ProductNameShortener {
    String getShortName(Product product);
}
