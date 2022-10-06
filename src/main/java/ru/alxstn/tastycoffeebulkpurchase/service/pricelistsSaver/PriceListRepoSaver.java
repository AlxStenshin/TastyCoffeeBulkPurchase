package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

@Component
public class PriceListRepoSaver {
    Logger logger = LogManager.getLogger(PriceListRepoSaver.class);

    private final ProductRepository repository;

    public PriceListRepoSaver(ProductRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void handlePriceList(final PriceListReceivedEvent event) {
        for (var p : event.getPriceList()) {
            if (repository.productExists(p.getName(), p.getProductCategory(), p.getProductSubCategory(), p.getProductPackage(), p.getSpecialMark(), p.getPrice()).isPresent()) {
                logger.info("Product already exist, skipping: " + p);
            } else {
                repository.save(p);
                logger.info("Saving new Product: " + p);
                // ToDo: Check if new product updates only productMark or price and emit corresponding event.
            }
        }
    }
}
