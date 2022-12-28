package ru.alxstn.tastycoffeebulkpurchase.service.priceListsUpdater;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.util.TastyCoffeePage;

import java.util.List;

@Service
public class WebScrapperPriceListUpdaterService implements PriceListUpdaterService {

    Logger logger = LogManager.getLogger(WebScrapperPriceListUpdaterService.class);
    private final ApplicationEventPublisher publisher;
    private final TastyCoffeePage tastyCoffeeWebPage;

    public WebScrapperPriceListUpdaterService(ApplicationEventPublisher newProductEventPublisher, TastyCoffeePage page) {
        this.publisher = newProductEventPublisher;
        this.tastyCoffeeWebPage = page;
    }

    @Override
    @EventListener
    public void handleNewProduct(final ProductFoundEvent event) {
        logger.info("New product obtained: " + event.getProduct().toString());
    }

    @Override
    @Scheduled(fixedRate = 3 * 60 * 60 * 1000, initialDelay = 500)
    public void updatePriceList() {
        List<Product> priceList = tastyCoffeeWebPage.buildPriceList();
        logger.info("Got " + priceList.size() + " items in price list");

        publisher.publishEvent(new PriceListReceivedEvent(this, priceList));
    }
}
