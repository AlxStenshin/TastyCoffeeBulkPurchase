package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BasicPurchaseFilterServiceTest {

    private static BasicPurchaseFilterService service;

    private static final Session session = new Session();
    private static final ProductPackage pack = new ProductPackage("Упаковка 250 г");
    private static final Map<Product, Integer> allPurchases = new HashMap<>();
    private static final List<Product> allProducts = List.of(
            new Product("fineGrind",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "Мелкий",
                    true),
            new Product("midGrind",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "Средний",
                    true),
            new Product("coarseGrind",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "Крупный",
                    true),
            new Product("Beans",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "Зерно",
                    true),
            new Product("Tea",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Чай",
                    "Subgroup",
                    "",
                    false),
            new Product("Choco",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Шоколад",
                    "Subgroup",
                    "",
                    false),
            new Product("Syrup",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Сиропы",
                    "Subgroup",
                    "",
                    false),
            new Product("Unknown",
                    new BigDecimal(1),
                    "none",
                    pack,
                    "Group",
                    "Subgroup",
                    "",
                    false)
    );


    @BeforeAll
    static void init() {
        service = new BasicPurchaseFilterService();
        for (var p : allProducts) {
            allPurchases.put(p, 1);
        }
    }

    @Test
    void shouldCorrectlyCreateService() {
        assertNotNull(service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Зерно", "Мелкий", "Средний", "Крупный", "Чай", "Шоколад", "Сиропы"})
    void shouldFilterAllButOneProduct(String targetProduct) {
        var properties = service.createAllEnabledProperties(session);
        properties.getRequiredProductTypes().stream()
                .filter(p -> !Objects.equals(p.getDescription(), targetProduct))
                .forEach(p -> p.setValue(false));

        Assertions.assertEquals(1, service.filterPurchases(properties, allPurchases).entrySet().size());
    }

}