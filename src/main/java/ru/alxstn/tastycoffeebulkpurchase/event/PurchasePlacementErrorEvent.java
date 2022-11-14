package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;

import java.util.List;

public class PurchasePlacementErrorEvent extends ApplicationEvent {
    private final List<Purchase> unsuccessfulPurchases;

    public PurchasePlacementErrorEvent(Object source, List<Purchase> unsuccessfulPurchases) {
        super(source);
        this.unsuccessfulPurchases = unsuccessfulPurchases;
    }

    public List<Purchase> getUnsuccessfulPurchases() {
        return unsuccessfulPurchases;
    }
}
