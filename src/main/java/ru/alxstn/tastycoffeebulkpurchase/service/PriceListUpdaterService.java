package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;

public interface PriceListUpdaterService {
    void updatePriceList();
    void handleNewProduct(final ProductFoundEvent event);
}
