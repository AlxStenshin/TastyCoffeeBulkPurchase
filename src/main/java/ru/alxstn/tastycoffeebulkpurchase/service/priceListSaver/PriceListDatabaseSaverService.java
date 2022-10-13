package ru.alxstn.tastycoffeebulkpurchase.service.priceListSaver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.PriceListSaverService;
import ru.alxstn.tastycoffeebulkpurchase.service.ProductNameShortener;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.List;

@Component
public class PriceListDatabaseSaverService implements PriceListSaverService {
    Logger logger = LogManager.getLogger(PriceListDatabaseSaverService.class);

    private final ProductRepository repository;
    private final DateTimeProvider dateTimeProvider;
    private final ProductNameShortener productNameShortener;

    public PriceListDatabaseSaverService(ProductRepository repository,
                                         DateTimeProvider dateTimeProvider, ProductNameShortener productNameShortener) {
        this.repository = repository;
        this.dateTimeProvider = dateTimeProvider;
        this.productNameShortener = productNameShortener;
    }

    @EventListener
    @Override
    public void handlePriceList(final PriceListReceivedEvent event) {
        repository.markAllNotActual();

        List<Product> toSave = event.getPriceList();
        toSave.forEach(p -> p.setDisplayName(productNameShortener.getShortName(p)));

        for (var product : toSave) {
            if (repository.productExists(product.getName(), product.getProductCategory(), product.getProductSubCategory(),
                    product.getProductPackage(), product.getSpecialMark(), product.getPrice()).isPresent()) {
                logger.info("Product already exist, updating timestamp: " + product);
                repository.update(product.getName(),
                        product.getProductCategory(),
                        product.getProductSubCategory(),
                        product.getProductPackage(),
                        product.getSpecialMark(),
                        product.getPrice(),
                        dateTimeProvider.getCurrentTimestamp());
            } else {
                repository.save(product);
                logger.info("Saving new Product: " + product);
                // ToDo: Check if new product updates only productMark or price and emit corresponding event.
            }
        }
    }
}