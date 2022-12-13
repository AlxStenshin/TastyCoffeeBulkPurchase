package ru.alxstn.tastycoffeebulkpurchase.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.NewProductDiscoveredEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductPriceUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductSpecialMarkUpdateEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BasicProductAnalyzerService implements ProductAnalyzerService {

    Logger logger = LogManager.getLogger(BasicProductAnalyzerService.class);
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;

    public BasicProductAnalyzerService(ProductRepository productRepository,
                                       ApplicationEventPublisher publisher) {
        this.productRepository = productRepository;
        this.publisher = publisher;
    }

    @Override
    public void analyzeNewProducts(List<Product> newProducts) {
        for (Product newProduct : newProducts) {
            logger.info("Analyzing New Product: " + newProduct);

            List<Product> similarProducts = productRepository.getProductsByNameCategorySubcategoryAndPackage(
                    newProduct.getName(),
                    newProduct.getProductCategory(),
                    newProduct.getProductSubCategory(),
                    newProduct.getProductPackage());

            similarProducts = similarProducts.stream()
                    .filter(Predicate.not(Product::isActual))
                    .sorted(Comparator.comparing(Product::getDateUpdated).reversed())
                    .collect(Collectors.toList());

            if (similarProducts.size() > 0) {
                Product latestSimilar = similarProducts.get(0);

                if (!Objects.equals(newProduct.getPrice(), latestSimilar.getPrice())) {
                    logger.info("Product Price Update Detected.");
                    publisher.publishEvent(new ProductPriceUpdateEvent(this, latestSimilar, newProduct));
                }

                if (!Objects.equals(newProduct.getSpecialMark(), latestSimilar.getSpecialMark())) {
                    logger.info("Product Special Mark Update Detected.");
                    publisher.publishEvent(new ProductSpecialMarkUpdateEvent(this, latestSimilar, newProduct));
                }
            }
            else {
                logger.info("New Product Detected.");
                publisher.publishEvent(new NewProductDiscoveredEvent(this, newProduct));
            }
        }
    }
}
