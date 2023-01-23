package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.*;
import ru.alxstn.tastycoffeebulkpurchase.model.ProductTypeFilter;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilters;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseFilterService;

import java.util.*;
import java.util.function.Predicate;


public class BasicPurchaseFilterService implements PurchaseFilterService {

    final List<String> productFormsCategories = List.of("Зерно", "Мелкий", "Средний", "Крупный");
    // ToDo: Obtain Product Types via Product Repository Distinct Request.
    final List<String> productTypeCategories = List.of("Чай", "Шоколад", "Сиропы");

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

    public SessionProductFilters createAllTypesWithState(Session session,
                                                         SessionProductFilterType filterType,
                                                         boolean state) {
        List<ProductTypeFilter> productTypes = new ArrayList<>(productFormsCategories.stream()
                .map(s -> new ProductTypeFilter(s, state))
                .toList());

        productTypes.addAll(productTypeCategories.stream()
                .map(s -> new ProductTypeFilter(s, state))
                .toList());

        SessionProductFilters types = new SessionProductFilters(productTypes, filterType);
        types.setSession(session);

        return types;
    }

    private Predicate<Product> buildPredicate(String type) {
        if (productFormsCategories.contains(type))
            return product -> product.getProductForm().equals(type);
        else if (productTypeCategories.contains(type))
            return product -> product.getProductCategory().equals(type);
        else return null;
    }

}
