package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver;

import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;

public interface PriceListUpdater {

    void updatePriceList();
    void handleNewProduct(final ProductFoundEvent event);
}
