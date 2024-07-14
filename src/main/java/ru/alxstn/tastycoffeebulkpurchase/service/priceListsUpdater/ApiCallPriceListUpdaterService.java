package ru.alxstn.tastycoffeebulkpurchase.service.priceListsUpdater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeeApi;

import java.util.List;

public class ApiCallPriceListUpdaterService implements PriceListUpdaterService {

    Logger logger = LogManager.getLogger(ApiCallPriceListUpdaterService.class);
    private final ApplicationEventPublisher publisher;
    private final TastyCoffeeApi tastyCoffeeApi;

    public ApiCallPriceListUpdaterService(ApplicationEventPublisher publisher, TastyCoffeeApi tastyCoffeeApi) {
        this.publisher = publisher;
        this.tastyCoffeeApi = tastyCoffeeApi;
    }

    @Override
    public void handleNewProduct(ProductFoundEvent event) {
        logger.info("New product obtained: {}", new ProductCaptionBuilder(event.getProduct())
                .createIconNameMarkPackagePriceView());
    }

    @Override
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000, initialDelay = 5000)
    public void updatePriceList() {
        try {
            List<Product> priceList = tastyCoffeeApi.buildPriceList();
            logger.info("Got {} items in price list", priceList.size());
            publisher.publishEvent(new PriceListReceivedEvent(this, priceList));
        } catch (Exception e) {
            logger.error("API Call parser exception:", e);
        }
    }
}
