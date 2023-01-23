package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.BasicPurchaseFilterService;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionPurchaseReportCreatorService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
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

        var allAccepted = requiredProductsService.createAllTypesWithState(
                session, SessionProductFilterType.DISCARD_FILTER, false);

        saveReport(session, report, allAccepted);
    }

    @Override
    public void placeOrderWithProductTypes(SessionProductFilters productTypes) {
        logger.info("Placing order for types: " + productTypes);
        Session session = productTypes.getSession();
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);
        if (currentSessionPurchases.size() > 0) {
            Map<Product, Integer> requiredPurchases = requiredProductsService.filterPurchases(productTypes, currentSessionPurchases);
            String report = buildReport(requiredPurchases);
            saveReport(session, report, productTypes);
        }
    }

    private String buildReport(Map<Product, Integer> purchases) {
        String report = purchases.entrySet().stream()
                .map(e -> e.getKey().getShortName() + " - " + e.getValue() + " шт.")
                .sorted()
                .collect(Collectors.joining("\n"));

        int totalCount = purchases.values().stream()
                        .reduce(Integer::sum).orElse(0);

        logger.info(report);
        logger.info("totalProductsCount: " + totalCount);
        // ToDo: Find Reason why it is not working
        return report;
    }

    private void saveReport(Session session, String report, SessionProductFilters productProperties) {
        StringBuilder fileName = new StringBuilder(session.getId() + "_" + session.getTitle() + "_");
        List<String> filteredProducts = productProperties.getProductTypeFilters().stream()
                .filter(ProductTypeFilter::getValue)
                .map(ProductTypeFilter::getDescription)
                .toList();

        if (!filteredProducts.isEmpty()) {
            fileName.append(productProperties.getFilterType().getShortDescription());
            for (String product : filteredProducts) {
                fileName.append(product);
                fileName.append(" ");
            }
            fileName.append(")");
        }

        fileName.append("_SessionReport.json");

        try (PrintWriter out = new PrintWriter(fileName.toString())) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
