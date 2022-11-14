package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;


public interface UnfinishedPurchasesNotifierService {
    void handleUnfinishedPurchases(PurchasePlacementErrorEvent event);
}
