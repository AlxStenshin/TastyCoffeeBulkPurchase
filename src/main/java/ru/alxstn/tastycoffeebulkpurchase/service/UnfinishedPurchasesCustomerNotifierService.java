package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;

public interface UnfinishedPurchasesCustomerNotifierService {
    void handleUnfinishedPurchases(PurchasePlacementErrorEvent event);
}
