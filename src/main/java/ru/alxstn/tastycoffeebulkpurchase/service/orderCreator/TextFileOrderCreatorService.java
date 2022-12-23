package ru.alxstn.tastycoffeebulkpurchase.service.orderCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.OrderCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TextFileOrderCreatorService implements OrderCreatorService {

    Logger logger = LogManager.getLogger(TextFileOrderCreatorService.class);

    private final PurchaseManagerService purchaseManagerService;

    public TextFileOrderCreatorService(PurchaseManagerService purchaseManagerService) {
        this.purchaseManagerService = purchaseManagerService;
    }

    @Override
    public void createOrder(Session session) {
        logger.info("Now building purchase summary for session " + session.getId() + ":" + session.getTitle());
        List<Purchase> currentSessionPurchases = purchaseManagerService.findAllPurchasesInSession(session)
                .stream()
                .filter(purchase -> purchase.getProduct().isActual() && purchase.getProduct().isAvailable())
                .toList();

        Map<Pair<Product, String>, List<Purchase>> groupedPurchases = currentSessionPurchases.stream()
                .collect(Collectors.groupingBy(purchase -> Pair.of(purchase.getProduct(), purchase.getProductForm())));

        Map<Pair<Product, String>, Integer> differentFormsProductsCount = groupedPurchases.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .map(Purchase::getCount)
                                .reduce(Integer::sum).orElse(0)));

        String report = differentFormsProductsCount.entrySet().stream().map(e ->
                        e.getKey().getFirst().getShortName() +
                                (e.getKey().getSecond().isBlank() ? "" : ", " + e.getKey().getSecond()) +
                                " - " +
                                e.getValue() + " шт.\n")
                .reduce(String::concat).orElse("");

        try (PrintWriter out = new PrintWriter(session.getId() + ".sessionReport.json")) {
            out.println(report);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
