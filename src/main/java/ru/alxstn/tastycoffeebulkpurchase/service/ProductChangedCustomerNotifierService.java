package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.ProductUpdateEvent;

public interface ProductChangedCustomerNotifierService {
    void handleChangedProducts(ProductUpdateEvent event);
}
