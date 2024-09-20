package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.PurchaseFilterService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TextFileOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;
    private final PurchaseFilterService purchaseFilterService;

    public TextFileOrderCreatorService(SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService,
                                       PurchaseFilterService purchaseFilterService) {
        this.sessionPurchaseReportCreatorService = sessionPurchaseReportCreatorService;
        this.purchaseFilterService = purchaseFilterService;
    }

    @Override
    public void placeFullOrder(Session session) {
        logger.info("Now Saving Purchase Summary for session " + session.getId() + ":" + session.getTitle());
        String report = buildReport(sessionPurchaseReportCreatorService.createPerProductReport(session));

        var allAccepted = purchaseFilterService.createFilter(
                session, SessionProductFilterType.DISCARD_FILTER, false);

        TextReportSaver.saveReport(session, report, "", Optional.ofNullable(allAccepted));
    }

    @Override
    public void placeOrderWithProductFilter(SessionProductFilters filter) {
        logger.info("Placing order for types: " + filter);
        Session session = filter.getSession();
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);
        if (currentSessionPurchases.size() > 0) {
            Map<Product, Integer> requiredPurchases = purchaseFilterService.filterPurchases(filter, currentSessionPurchases);
            String report = buildReport(requiredPurchases);
            logger.info("Report Created successfully: \n\n" + report);
            TextReportSaver.saveReport(session, report, "", Optional.of(filter));
        } else logger.info("No purchases found, report building skipped.");
    }

    private String buildReport(Map<Product, Integer> purchases) {
        String report = purchases.entrySet().stream()
                .map(e -> new ProductCaptionBuilder(e.getKey()).createCatSubcatNamePackageFormView() +
                        " - " + e.getValue() + " шт.")
                .sorted()
                .collect(Collectors.joining("\n"));

        int totalCount = purchases.values().stream()
                .reduce(Integer::sum).orElse(0);

        logger.info(report);
        logger.info("totalProductsCount: " + totalCount);
        return report;
    }
}
