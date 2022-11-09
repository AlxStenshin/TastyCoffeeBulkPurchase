package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.OrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.priceListsUpdater.webscrapper.TastyCoffeePage;

import java.util.List;

@Service
public class WebPageOrderCreator implements OrderCreatorService {

    private final TastyCoffeePage tastyCoffeePage;
    private final PurchaseRepository purchaseRepository;
    private final SessionRepository sessionRepository;

    public WebPageOrderCreator(TastyCoffeePage tastyCoffeePage,
                               PurchaseRepository purchaseRepository,
                               SessionRepository sessionRepository) {
        this.tastyCoffeePage = tastyCoffeePage;
        this.purchaseRepository = purchaseRepository;
        this.sessionRepository = sessionRepository;
    }

    public void createOrder() {
        tastyCoffeePage.login();
        Session currentSession = sessionRepository.getCurrentSession();
        List<Purchase> currentSessionPurchases = purchaseRepository.findAllPurchasesInSession(currentSession);
        List<Purchase> unrecognizedPurchases = tastyCoffeePage.placeOrder(currentSessionPurchases);
    }

}
