package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.event.PurchasePlacementErrorEvent;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.PurchaseFilterService;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePage;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebPageOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(WebPageOrderCreatorService.class);

    private final ApplicationEventPublisher publisher;
    private final TastyCoffeePage tastyCoffeePage;
    private final SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;
    private final PurchaseFilterService purchaseFilterService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WebPageOrderCreatorService(ApplicationEventPublisher publisher,
                                      TastyCoffeePage tastyCoffeePage,
                                      PurchaseFilterService purchaseFilterService,
                                      SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService) {
        this.publisher = publisher;
        this.tastyCoffeePage = tastyCoffeePage;
        this.purchaseFilterService = purchaseFilterService;
        this.sessionPurchaseReportCreatorService = sessionPurchaseReportCreatorService;
    }

    public void placeFullOrder(Session session) {
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

    @Override
    public void placeOrderWithProductFilter(SessionProductFilters discardedProductProperties) {
        logger.info("Placing order without products: " + discardedProductProperties);
        Session session = discardedProductProperties.getSession();
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);
        if (currentSessionPurchases.size() > 0) {
            Map<Product, Integer> requiredPurchases = purchaseFilterService.
                    filterPurchases(discardedProductProperties, currentSessionPurchases);
            executorService.execute(() -> {
                var unfinishedPurchasesProducts = tastyCoffeePage.placeOrder(requiredPurchases);
                publisher.publishEvent(new PurchasePlacementErrorEvent(this, unfinishedPurchasesProducts));
            });
            logger.info("Order Placed!");
        }
    }

}
