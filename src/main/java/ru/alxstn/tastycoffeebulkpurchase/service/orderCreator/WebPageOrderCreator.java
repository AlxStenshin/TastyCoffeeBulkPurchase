package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchaseSummaryNotificationEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.OrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebPageOrderCreator implements OrderCreatorService {

    Logger logger = LogManager.getLogger(WebPageOrderCreator.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeePage tastyCoffeePage;
    private final PurchaseRepository purchaseRepository;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WebPageOrderCreator(ApplicationEventPublisher publisher,
                               TastyCoffeePage tastyCoffeePage,
                               PurchaseRepository purchaseRepository) {
        this.publisher = publisher;
        this.tastyCoffeePage = tastyCoffeePage;
        this.purchaseRepository = purchaseRepository;
    }

    public void createOrder(Session session) {
        logger.info("Now placing order from current session " + session.getId() + ":" + session.getTitle());
        List<Purchase> currentSessionPurchases = purchaseRepository.findAllPurchasesInSession(session);
        publisher.publishEvent(new PurchaseSummaryNotificationEvent(this, session, currentSessionPurchases));

        executorService.execute(() -> {
            List<Purchase> unfinishedPurchases = tastyCoffeePage.placeOrder(currentSessionPurchases);
            publisher.publishEvent(new PurchasePlacementErrorEvent(this, unfinishedPurchases));
        });
        logger.info("Order Placed!");
    }

}
