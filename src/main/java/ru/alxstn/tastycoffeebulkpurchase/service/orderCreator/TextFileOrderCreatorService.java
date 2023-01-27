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

        saveReport(session, report, Optional.ofNullable(allAccepted));
    }

    @Override
    public void placeOrderWithProductFilter(SessionProductFilters filter) {
        logger.info("Placing order for types: " + filter);
        Session session = filter.getSession();
        var currentSessionPurchases = sessionPurchaseReportCreatorService.createPerProductReport(session);
        if (currentSessionPurchases.size() > 0) {
            Map<Product, Integer> requiredPurchases = purchaseFilterService.filterPurchases(filter, currentSessionPurchases);
            String report = buildReport(requiredPurchases);
            saveReport(session, report, Optional.of(filter));
        }
    }

    private String buildReport(Map<Product, Integer> purchases) {
        String report = purchases.entrySet().stream()
                .map(e -> new ProductCaptionBuilder(e.getKey()).createCatSubcatNameMarkPackageView() +
                        " - " + e.getValue() + " шт.")
                .sorted()
                .collect(Collectors.joining("\n"));

        int totalCount = purchases.values().stream()
                        .reduce(Integer::sum).orElse(0);

        logger.info(report);
        logger.info("totalProductsCount: " + totalCount);
        // ToDo: Find Reason why it is not working
        return report;
    }

    private void saveReport(Session session, String report, Optional<SessionProductFilters> productProperties) {
        StringBuilder fileName = new StringBuilder(session.getId() + "_" + session.getTitle() + "_");
        if (productProperties.isPresent()) {

            List<String> filteredProducts = productProperties.get().getProductTypeFilters().stream()
                    .filter(ProductTypeFilter::getValue)
                    .map(ProductTypeFilter::getDescription)
                    .toList();

            if (!filteredProducts.isEmpty()) {
                fileName.append(productProperties.get().getFilterType().getShortDescription());
                for (String product : filteredProducts) {
                    fileName.append(product);
                    fileName.append(" ");
                }
                fileName.append(")_");
            }
        }

        fileName.append("SessionReport.json");

        try (PrintWriter out = new PrintWriter(fileName.toString())) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
