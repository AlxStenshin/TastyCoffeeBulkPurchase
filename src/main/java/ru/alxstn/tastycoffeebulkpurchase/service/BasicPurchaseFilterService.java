package ru.alxstn.tastycoffeebulkpurchase.service;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseFilterService;

import java.util.*;
import java.util.function.Predicate;

@Service
public class BasicPurchaseFilterService implements PurchaseFilterService {


    private final ProductManagerService productManagerService;

    public BasicPurchaseFilterService(ProductManagerService productManagerService) {
        this.productManagerService = productManagerService;
    }

    @Override
    public Map<Product, Integer> filterPurchases(SessionProductFilters discardedProductProperties, Map<Product, Integer> allPurchases) {

        Map<Product, Integer> requiredPurchases = new HashMap<>(allPurchases);

        List<String> discardedTypes = discardedProductProperties.getProductTypeFilters().stream()
                .filter(ProductTypeFilter::getValue)
                .map(ProductTypeFilter::getDescription)
                .toList();

        for (var type : discardedTypes) {
            List<Product> discardedProducts = requiredPurchases.keySet().stream()
                    .filter(Objects.requireNonNull(buildPredicate(type)))
                    .toList();

            for (Product p : discardedProducts) {
                requiredPurchases.remove(p);
            }
        }

        return requiredPurchases;
    }

    public SessionProductFilters createFilter(Session session,
                                              SessionProductFilterType filterType,
                                              boolean targetState) {
        List<ProductTypeFilter> productTypes = new ArrayList<>(productManagerService.findAllActiveCategories()
                .stream()
                .map(s -> new ProductTypeFilter(s, targetState))
                .toList());

        productTypes.addAll(productManagerService.findAllActiveProductForms()
                .stream()
                .map(s -> new ProductTypeFilter(s, targetState))
                .toList());

        SessionProductFilters types = new SessionProductFilters(productTypes, filterType);
        types.setSession(session);

        return types;
    }

    private Predicate<Product> buildPredicate(String type) {
        if (productManagerService.findAllActiveProductForms().contains(type))
            return product -> product.getProductForm().equals(type);

        else if (productManagerService.findAllActiveCategories().contains(type))
            return product -> product.getProductCategory().equals(type);

        else return null;
    }

}
