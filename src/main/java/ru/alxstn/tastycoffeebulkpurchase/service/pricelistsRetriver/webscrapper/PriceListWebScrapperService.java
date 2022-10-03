package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.PriceListUpdater;

import java.util.List;

@Service
public class PriceListWebScrapperService implements PriceListUpdater {

    Logger logger = LogManager.getLogger(PriceListWebScrapperService.class);

    private final ApplicationEventPublisher priceListPublisher;
    private final TastyCoffeePage tastyCoffeeWebPage;

    public PriceListWebScrapperService(ApplicationEventPublisher newProductEventPublisher, TastyCoffeePage page) {
        this.priceListPublisher = newProductEventPublisher;
        this.tastyCoffeeWebPage = page;
    }

    @EventListener
    public void handleNewProduct(final ProductFoundEvent event) {
        logger.info("New product obtained: " + event.getProduct().toString());
    }

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void updatePriceList() {
        tastyCoffeeWebPage.login();
        List<Product> priceList = tastyCoffeeWebPage.buildPriceList();
        logger.info("Got " + priceList.size() + " items in price list");

        priceListPublisher.publishEvent(new PriceListReceivedEvent(this, priceList));
    }
}
