package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;

import java.util.List;

public class PurchaseSummaryNotificationEvent extends ApplicationEvent {
    private final List<Purchase> currentSessionPurchases;

    public PurchaseSummaryNotificationEvent(Object source,
                                            List<Purchase> currentSessionPurchases) {
        super(source);
        this.currentSessionPurchases = currentSessionPurchases;
    }

    public List<Purchase> getCurrentSessionPurchases() {
        return currentSessionPurchases;
    }

}
