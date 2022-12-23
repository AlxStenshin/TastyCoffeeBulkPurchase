package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.OrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebPageOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(WebPageOrderCreatorService.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeePage tastyCoffeePage;
    private final PurchaseManagerService purchaseManagerService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WebPageOrderCreatorService(ApplicationEventPublisher publisher,
                                      TastyCoffeePage tastyCoffeePage,
                                      PurchaseManagerService purchaseManagerService) {
        this.publisher = publisher;
        this.tastyCoffeePage = tastyCoffeePage;
        this.purchaseManagerService = purchaseManagerService;
    }

    public void createOrder(Session session) {
        logger.info("Now placing order from current session " + session.getId() + ":" + session.getTitle());
        List<Purchase> currentSessionPurchases = purchaseManagerService.findAllPurchasesInSession(session)
                .stream()
                .filter(purchase -> purchase.getProduct().isActual() && purchase.getProduct().isAvailable())
                .toList();

        publisher.publishEvent(new PurchaseSummaryNotificationEvent(this, currentSessionPurchases));

        executorService.execute(() -> {
            List<Purchase> unfinishedPurchases = tastyCoffeePage.placeOrder(currentSessionPurchases);
            publisher.publishEvent(new PurchasePlacementErrorEvent(this, unfinishedPurchases));
        });
        logger.info("Order Placed!");
    }

}
