package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.PurchaseEntry;

import java.util.List;

public class PurchasePlacementErrorEvent extends ApplicationEvent {
    private final List<PurchaseEntry> unsuccessfulPurchases;

    public PurchasePlacementErrorEvent(Object source, List<PurchaseEntry> unsuccessfulPurchases) {
        super(source);
        this.unsuccessfulPurchases = unsuccessfulPurchases;
    }

    public List<PurchaseEntry> getUnsuccessfulPurchases() {
        return unsuccessfulPurchases;
    }
}
