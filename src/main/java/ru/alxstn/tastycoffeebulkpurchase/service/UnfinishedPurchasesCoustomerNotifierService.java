package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;

public interface UnfinishedPurchasesCoustomerNotifierService {
    void handleUnfinishedPurchases(PurchasePlacementErrorEvent event);
}
