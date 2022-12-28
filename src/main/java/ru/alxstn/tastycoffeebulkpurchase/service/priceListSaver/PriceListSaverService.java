package ru.alxstn.tastycoffeebulkpurchase.service.priceListSaver;

import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;


public interface PriceListSaverService {
    void handlePriceList(PriceListReceivedEvent priceListEvent);
}
