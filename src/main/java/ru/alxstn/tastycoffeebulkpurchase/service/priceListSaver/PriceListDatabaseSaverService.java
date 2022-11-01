package ru.alxstn.tastycoffeebulkpurchase.service.priceListSaver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.event.PriceListReceivedEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductPackageRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.PriceListSaverService;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PriceListDatabaseSaverService implements PriceListSaverService {
    Logger logger = LogManager.getLogger(PriceListDatabaseSaverService.class);

    private final ProductRepository productRepository;
    private final ProductPackageRepository productPackageRepository;
    private final DateTimeProvider dateTimeProvider;

    public PriceListDatabaseSaverService(ProductRepository productRepository,
                                         ProductPackageRepository productPackageRepository,
                                         DateTimeProvider dateTimeProvider) {
        this.productRepository = productRepository;
        this.productPackageRepository = productPackageRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    @EventListener
    @Override
    public void handlePriceList(final PriceListReceivedEvent event) {
        productRepository.markAllNotActual();

        List<Product> productsToSave = event.getPriceList();
        Set<ProductPackage> packagesToSave = new HashSet<>();

        // Product Package Persistence
        for (var product: productsToSave) {
            packagesToSave.add(product.getProductPackage());
        }
        for (var pack : packagesToSave) {
            Example<ProductPackage> example = Example.of(pack, ExampleMatcher.matchingAll().withIgnorePaths("id"));
            if (!productPackageRepository.exists(example))
                productPackageRepository.saveAndFlush(pack);
        }

        // Product Persistence
        for (var product : productsToSave) {
            ProductPackage productPackage =
                    productPackageRepository.getProductPackageByDescription(product.getProductPackage().getDescription());
            product.setProductPackage(productPackage);

            if (productRepository.productExists(product.getName(), product.getProductCategory(), product.getProductSubCategory(),
                    product.getProductPackage(), product.getSpecialMark(), product.getPrice()).isPresent()) {
                logger.info("Product already exist, updating timestamp: " + product);

                productRepository.update(product.getName(),
                        product.getProductCategory(),
                        product.getProductSubCategory(),
                        product.getProductPackage(),
                        product.getSpecialMark(),
                        product.getPrice(),
                        dateTimeProvider.getCurrentTimestamp());
            } else {
                productRepository.save(product);
                logger.info("Saving new Product: " + product);
                // ToDo: Check if new product updates only productMark or price and emit corresponding event.
            }
        }
    }
}