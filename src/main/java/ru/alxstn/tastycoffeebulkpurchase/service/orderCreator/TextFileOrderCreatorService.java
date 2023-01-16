package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductProperties;
import ru.alxstn.tastycoffeebulkpurchase.service.BasicPurchaseFilterService;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TextFileOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService;
    private final BasicPurchaseFilterService requiredProductsService;

    public TextFileOrderCreatorService(SessionPurchaseReportCreatorService sessionPurchaseReportCreatorService) {
        this.sessionPurchaseReportCreatorService = sessionPurchaseReportCreatorService;
        this.requiredProductsService = new BasicPurchaseFilterService();
    }

    @Override
    public void placeFullOrder(Session session) {
        logger.info("Now Saving Purchase Summary for session " + session.getId() + ":" + session.getTitle());
        String report = buildReport(sessionPurchaseReportCreatorService.createPerProductReport(session));
        saveReport(session, report);
    }

    @Override
    public void placeOrderWithProductTypes(RequiredProductProperties productTypes) {
        logger.info("Placing order for types: " + productTypes);
        Session session = productTypes.getSession();
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);
        if (currentSessionPurchases.size() > 0) {
            Map<Product, Integer> requiredPurchases = requiredProductsService.filterPurchases(productTypes, currentSessionPurchases);
            String report = buildReport(requiredPurchases);
            saveReport(session, report);
        }
    }

    private String buildReport(Map<Product, Integer> purchases) {
        return purchases.entrySet().stream()
                .map(e -> e.getKey().getShortName() + " - " + e.getValue() + " шт.")
                .collect(Collectors.joining("\n"));
    }

    private void saveReport(Session session, String report) {
        try (PrintWriter out = new PrintWriter(session.getId() + "." + session.getTitle() + ".SessionReport.json")) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
