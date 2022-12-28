package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;

public class PurchasePlacementErrorEvent extends ApplicationEvent {
    private final List<Product> unsuccessfulPurchases;

    public PurchasePlacementErrorEvent(Object source, List<Product> unsuccessfulPurchases) {
        super(source);
        this.unsuccessfulPurchases = unsuccessfulPurchases;
    }

    public List<Product> getUnsuccessfulPurchases() {
        return unsuccessfulPurchases;
    }
}
