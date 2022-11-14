package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.OrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.TastyCoffeePage;

import java.util.List;

@Service
public class WebPageOrderCreator implements OrderCreatorService {

    Logger logger = LogManager.getLogger(WebPageOrderCreator.class);
    private final TastyCoffeePage tastyCoffeePage;
    private final PurchaseRepository purchaseRepository;

    public WebPageOrderCreator(TastyCoffeePage tastyCoffeePage,
                               PurchaseRepository purchaseRepository) {
        this.tastyCoffeePage = tastyCoffeePage;
        this.purchaseRepository = purchaseRepository;
    }

    public void createOrder(Session session) {
        tastyCoffeePage.login();
        tastyCoffeePage.resetOrder();
        logger.info("Now placing order from current session " + session.getId() + ":" + session.getTitle());
        List<Purchase> currentSessionPurchases = purchaseRepository.findAllPurchasesInSession(session);
        List<Purchase> unrecognizedPurchases = tastyCoffeePage.placeOrder(currentSessionPurchases);
        logger.info("Order Placed!");
    }

}
