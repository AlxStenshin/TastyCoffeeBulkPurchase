package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebPageOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(WebPageOrderCreatorService.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeePage tastyCoffeePage;
    private final SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WebPageOrderCreatorService(ApplicationEventPublisher publisher,
                                      TastyCoffeePage tastyCoffeePage,
                                      SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService) {
        this.publisher = publisher;
        this.tastyCoffeePage = tastyCoffeePage;
        this.sessionPurchaseReportCreatorService = sessionPurchaseReportCreatorService;
    }

    public void createOrder(Session session) {
        logger.info("Now placing order from current session " + session.getId() + ":" + session.getTitle());
        // ToDo: add not actual and not available products to unfinished purchases
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);

        if (currentSessionPurchases.size() > 0) {
            executorService.execute(() -> {
                var unfinishedPurchases = tastyCoffeePage.placeOrder(currentSessionPurchases);
                publisher.publishEvent(new PurchasePlacementErrorEvent(this, unfinishedPurchases));
            });
            logger.info("Order Placed!");
        }
    }

}
