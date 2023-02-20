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
import ru.alxstn.tastycoffeebulkpurchase.model.ProductCaptionBuilder;
import ru.alxstn.tastycoffeebulkpurchase.service.NewProductAnalyzerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceListDatabaseSaverService implements PriceListSaverService {
    Logger logger = LogManager.getLogger(PriceListDatabaseSaverService.class);

    private final ProductManagerService productManagerService;
    private final NewProductAnalyzerService newProductAnalyzerService;
    private final DateTimeProvider dateTimeProvider;

    public PriceListDatabaseSaverService(ProductManagerService productManagerService,
                                         NewProductAnalyzerService newProductAnalyzerService,
                                         DateTimeProvider dateTimeProvider) {
        this.productManagerService = productManagerService;
        this.newProductAnalyzerService = newProductAnalyzerService;
        this.dateTimeProvider = dateTimeProvider;
    }

    @EventListener
    @Override
    public void handlePriceList(final PriceListReceivedEvent event) {
        productManagerService.markAllNotActual();
        List<Product> productsToSave = event.getPriceList();

        // Product Package Persistence
        for (var pack : productsToSave.stream().map(Product::getProductPackage).collect(Collectors.toSet())) {
            Example<ProductPackage> example = Example.of(pack, ExampleMatcher.matchingAll().withIgnorePaths("id"));
            if (!productManagerService.productPackageExists(example)) {
                logger.info("Saving Product Package: " + pack);
                productManagerService.save(pack);
            }
        }

        // Product Persistence
        List<Product> newProducts = new ArrayList<>();
        for (var product : productsToSave) {
            ProductPackage productPackage =
                    productManagerService.getProductPackageByDescription(product.getProductPackage().getDescription());
            product.setProductPackage(productPackage);

            if (productManagerService.productExists(
                    product.getName(),
                    product.getProductCategory(),
                    product.getProductSubCategory(),
                    product.getProductPackage(),
                    product.getSpecialMark(),
                    product.getProductForm(),
                    product.getPrice()
                    ).isPresent()) {
                logger.info("Product already exist, updating timestamp: " + new ProductCaptionBuilder(product).createIconNameMarkPackageFormPriceView());
                productManagerService.updateProduct(
                        product.getName(),
                        product.getProductCategory(),
                        product.getProductSubCategory(),
                        product.getProductPackage(),
                        product.getSpecialMark(),
                        product.getProductForm(),
                        product.getPrice(),
                        dateTimeProvider.getCurrentTimestamp());
            } else {
                newProducts.add(product);
                productManagerService.save(product);
                logger.info("Saving new Product: " + new ProductCaptionBuilder(product).createIconNameMarkPackageFormPriceView());
            }
        }
        newProductAnalyzerService.analyzeNewProducts(newProducts);
    }

}