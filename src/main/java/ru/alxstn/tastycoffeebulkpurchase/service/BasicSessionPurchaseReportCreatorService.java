package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.service.orderCreator.TextFileOrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BasicSessionPurchaseReportCreatorService implements SessionPurchaseReportCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final PurchaseManagerService purchaseManagerService;

    public BasicSessionPurchaseReportCreatorService(PurchaseManagerService purchaseManagerService) {
        this.purchaseManagerService = purchaseManagerService;
    }

    @Override
    public List<PurchaseEntry> createPerProductReport(Session session) {
        logger.info("Now Building Per-Product Purchase Summary for session " + session.getId() + ":" + session.getTitle());
        List<Purchase> currentSessionPurchases = purchaseManagerService.findAllPurchasesInSession(session)
                .stream()
                .filter(purchase -> purchase.getProduct().isActual() && purchase.getProduct().isAvailable())
                .toList();

        Map<Pair<Product, String>, List<Purchase>> groupedPurchases = currentSessionPurchases.stream()
                .collect(Collectors.groupingBy(purchase -> Pair.of(purchase.getProduct(), purchase.getProductForm())));

        return groupedPurchases.entrySet().stream()
                .map(e -> new PurchaseEntry(
                        e.getKey().getFirst(),
                        e.getKey().getSecond(),
                        e.getValue().stream()
                                .map(Purchase::getCount)
                                .reduce(Integer::sum).orElse(0)))
                .toList();
    }

    @Override
    public List<CustomerPurchaseEntry> createPerCustomerReport(Session session) {
        return null;
    }
}
