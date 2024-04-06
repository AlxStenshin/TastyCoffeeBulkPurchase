package ru.alxstn.tastycoffeebulkpurchase.service;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType.ACCEPT_FILTER;
import static ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType.DISCARD_FILTER;

@Service
public class BasicPurchaseFilterService implements PurchaseFilterService {


    private final ProductManagerService productManagerService;

    public BasicPurchaseFilterService(ProductManagerService productManagerService) {
        this.productManagerService = productManagerService;
    }

    @Override
    public Map<Product, Integer> filterPurchases(SessionProductFilters filter,
                                                 Map<Product, Integer> allPurchases) {
        Map<Product, Integer> requiredPurchases = new HashMap<>(allPurchases);

        List<String> discardedTypes = filter.getProductTypeFilters().stream()
                .filter(ProductTypeFilter::getValue)
                .map(ProductTypeFilter::getDescription)
                .toList();

        List<Product> filteredProducts = new ArrayList<>();
        for (var type : discardedTypes) {
            filteredProducts = requiredPurchases.keySet().stream()
                    .filter(Objects.requireNonNull(buildPredicate(type)))
                    .toList();
        }

        // Removing listed product
        if (filter.getFilterType() == DISCARD_FILTER) {
            for (Product p : filteredProducts) {
                requiredPurchases.remove(p);
            }
        }

        // Bypassing only listed products
        else if (filter.getFilterType() == ACCEPT_FILTER) {
            List<Product> finalFilteredProducts = filteredProducts;
            requiredPurchases = requiredPurchases.entrySet().stream()
                    .filter(e -> finalFilteredProducts.contains(e.getKey()) && e.getKey().isAvailable() && e.getKey().isActual())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return requiredPurchases;
    }

    public SessionProductFilters createFilter(Session session,
                                              SessionProductFilterType filterType,
                                              boolean targetState) {
        List<ProductTypeFilter> productTypes = new ArrayList<>(productManagerService.findAllActiveCategories()
                .stream()
                .filter(Predicate.not(String::isBlank))
                .map(s -> new ProductTypeFilter(s, targetState))
                .toList());

        productTypes.addAll(productManagerService.findAllActiveProductForms()
                .stream()
                .map(s -> new ProductTypeFilter(s, targetState))
                .toList());

        SessionProductFilters types = new SessionProductFilters(productTypes, filterType);
        types.setSession(session);
        types.setFilterType(filterType);

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
