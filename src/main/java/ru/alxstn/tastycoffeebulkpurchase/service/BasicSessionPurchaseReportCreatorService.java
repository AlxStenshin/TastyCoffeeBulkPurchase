package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.TextFileOrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BasicSessionPurchaseReportCreatorService implements SessionPurchaseReportCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final PurchaseManagerService purchaseManagerService;

    public BasicSessionPurchaseReportCreatorService(PurchaseManagerService purchaseManagerService) {
        this.purchaseManagerService = purchaseManagerService;
    }

    @Override
    public Map<Product, Integer> createPerProductReport(Session session) {
        logger.info("Now Building Per-Product Purchase Summary for session " + session.getId() + ":" + session.getTitle());

        List<Purchase> currentSessionPurchases = purchaseManagerService.findAllPurchasesInSession(session)
                .stream()
                .filter(purchase -> purchase.getProduct().isActual() && purchase.getProduct().isAvailable())
                .toList();

        Map<Product, Integer> totalProductCount = new HashMap<>();
        for (Purchase p : currentSessionPurchases) {
            totalProductCount.put(p.getProduct(), totalProductCount.getOrDefault(p.getProduct(), 0) + p.getCount());
        }
        return totalProductCount;
    }

}
