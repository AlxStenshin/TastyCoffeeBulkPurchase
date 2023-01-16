package ru.alxstn.tastycoffeebulkpurchase.service;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.RequiredProductType;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseFilterService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class BasicPurchaseFilterService implements PurchaseFilterService {

    final List<String> productFormsCategories = List.of("Зерно", "Мелкий", "Средний", "Крупный");
    final List<String> productTypeCategories = List.of("Чай", "Шоколад", "Сиропы");

    @Override
    public Map<Product, Integer> filterPurchases(RequiredProductProperties requiredTypes, Map<Product, Integer> allPurchases) {

        Map<Product, Integer> requiredPurchases = new HashMap<>();

        List<String> acceptedTypes = requiredTypes.getRequiredProductTypes().stream()
                .filter(RequiredProductType::getValue)
                .map(RequiredProductType::getDescription)
                .toList();

        for (var type : acceptedTypes) {
            requiredPurchases.putAll(allPurchases.entrySet().stream()
                    .filter(purchase -> allPurchases.keySet().stream().filter(Objects.requireNonNull(buildPredicate(type))).toList().contains(purchase.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        return requiredPurchases;
    }

    public RequiredProductProperties createAllEnabledProperties(Session session) {

        List<RequiredProductType> productTypes = new ArrayList<>(productFormsCategories.stream()
                .map(s -> new RequiredProductType(s, true))
                .toList());

        productTypes.addAll(productTypeCategories.stream()
                .map(s -> new RequiredProductType(s, true))
                .toList());

        RequiredProductProperties types = new RequiredProductProperties(productTypes);
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
