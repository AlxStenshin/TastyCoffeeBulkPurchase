package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;

public class PurchaseSummaryNotificationEvent extends ApplicationEvent {
    private final List<Purchase> currentSessionPurchases;
    private final Session session;

    public PurchaseSummaryNotificationEvent(Object source,
                                            Session session,
                                            List<Purchase> currentSessionPurchases) {
        super(source);
        this.session = session;
        this.currentSessionPurchases = currentSessionPurchases;
    }

    public List<Purchase> getCurrentSessionPurchases() {
        return currentSessionPurchases;
    }

    public Session getSession() {
        return session;
    }
}
