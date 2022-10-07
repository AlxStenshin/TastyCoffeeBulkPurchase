package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;


public interface PriceListSaverService {
    void handlePriceList(PriceListReceivedEvent priceListEvent);
}
