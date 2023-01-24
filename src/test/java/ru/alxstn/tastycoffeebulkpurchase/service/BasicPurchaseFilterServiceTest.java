package ru.alxstn.tastycoffeebulkpurchase.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.ProductPackage;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.model.SessionProductFilterType;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.ProductManagerService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class BasicPurchaseFilterServiceTest {

    @Mock
    ProductManagerService productManagerService;

    @InjectMocks
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

    @BeforeEach
    void init() {
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
    void shouldFilterOneProduct(String targetType) {

        when(productManagerService.findAllActiveCategories())
                .thenReturn(List.of("Чай", "Шоколад", "Сиропы"));

        when(productManagerService.findAllActiveProductForms())
                .thenReturn(List.of("Зерно", "Мелкий", "Средний", "Крупный"));

        var properties = service.createFilter(
                session, SessionProductFilterType.DISCARD_FILTER, false);

        properties.getProductTypeFilters().stream()
                .filter(p -> Objects.equals(p.getDescription(), targetType))
                .forEach(p -> p.setValue(true));
        var result = service.filterPurchases(properties, allPurchases);

        Assertions.assertEquals(allProducts.size() - 1, result.entrySet().size());
        Assertions.assertTrue(result.entrySet()
                .stream().noneMatch(e -> e.getKey().getProductCategory().equals(targetType) ||
                        e.getKey().getProductForm().equals(targetType)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Зерно", "Мелкий", "Средний", "Крупный", "Чай", "Шоколад", "Сиропы"})
    void shouldFilterAllButOneProduct(String targetType) {

        when(productManagerService.findAllActiveCategories())
                .thenReturn(List.of("Чай", "Шоколад", "Сиропы"));

        when(productManagerService.findAllActiveProductForms())
                .thenReturn(List.of("Зерно", "Мелкий", "Средний", "Крупный"));

        var properties = service.createFilter(
                session, SessionProductFilterType.DISCARD_FILTER, false);

        properties.getProductTypeFilters().stream()
                .filter(p -> Objects.equals(p.getDescription(), targetType))
                .forEach(p -> p.setValue(true));
        var result = service.filterPurchases(properties, allPurchases);

        Assertions.assertEquals(allProducts.size() - 1, result.entrySet().size());
        Assertions.assertTrue(result.entrySet()
                .stream().noneMatch(e -> e.getKey().getProductCategory().equals(targetType) ||
                        e.getKey().getProductForm().equals(targetType)));
    }

}