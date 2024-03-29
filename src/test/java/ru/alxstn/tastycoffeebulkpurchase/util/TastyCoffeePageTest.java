package ru.alxstn.tastycoffeebulkpurchase.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(TastyCoffeeConfigProperties.class)
@TestPropertySource("classpath:application.properties")
@TestPropertySource("classpath:secrets-test.properties")
class TastyCoffeePageTest {

    Logger logger = LogManager.getLogger(TastyCoffeePageTest.class);

    @Autowired
    TastyCoffeeConfigProperties tastyCoffeeTestConfig;

    @Mock
    ApplicationEventPublisher publisher;

    private TastyCoffeePage webPage;

    @BeforeEach
    public void init() {
        webPage = new TastyCoffeePage(tastyCoffeeTestConfig, publisher);
    }

    @Test
    void shouldCorrectlyInjectConfigurationProperties() {
        assertEquals("https://tastycoffee.ru", tastyCoffeeTestConfig.getUrl());
        assertNotNull(tastyCoffeeTestConfig.getUserName());
        assertNotNull(tastyCoffeeTestConfig.getPassword());
    }


    @Test
    void shouldCorrectlyLoginAndLogout() {
        Assertions.assertDoesNotThrow(() -> webPage.login());
        Assertions.assertDoesNotThrow(() -> webPage.logout());
    }

    @Test
    //@Disabled
    void shouldCorrectlyAddDecaffToPurchaseList() {
        Product decaf250Beans = new Product("Колумбия Декаф",
                new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ ЭСПРЕССО",
                "Кофе без кофеина",
                "Зерно",
                true);

        Map<Product, Integer> purchases = new HashMap<>();

        purchases.put(decaf250Beans, 5);
        Assertions.assertDoesNotThrow(() -> {
            List<Product> leftovers = webPage.placeOrder(purchases);
            assertTrue(leftovers.isEmpty());
        });
    }

    @Test
    //@Disabled
    void shouldCorrectlyAddBerryToPurchaseList() {
        Product berry250Beans = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "Зерно",
                true);

        Product berry250Coarse = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "Крупный",
                true);

        Product berry250Fine = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "Мелкий",
                true);

        Product berry250Medium = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "Средний",
                true);

        Product berry2000 = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 2 кг"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "",
                true);

        Product berry1000 = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 1 кг"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "",
                true);

        Map<Product, Integer> purchases = new HashMap<>();

        purchases.put(berry2000, 10);
        purchases.put(berry1000, 20);
        purchases.put(berry250Beans, 30);

        purchases.put(berry250Fine, 20);
        purchases.put(berry250Medium, 10);
        purchases.put(berry250Coarse, 10);

        Assertions.assertDoesNotThrow(() -> {
            List<Product> leftovers = webPage.placeOrder(purchases);
            assertEquals(0, leftovers.size());
        });
    }

    @Test
    //@Disabled
    void shouldCorrectlyObtainPriceListAndPlaceOrder() {
        // All functionality combined in one test because of parser high time-consuming behavior
        Assertions.assertDoesNotThrow(() -> {
            logger.info("Now Obtaining PriceList");
            List<Product> obtainedPriceList = webPage.buildPriceList().stream()
                    .filter(Product::isAvailable)
                    .filter(Product::isActual)
                    .toList();

            Map<Product, Integer> purchases = new HashMap<>();
            obtainedPriceList.forEach(p -> purchases.put(p, 5));

            logger.info("Now Building Order");
            List<Product> leftovers = webPage.placeOrder(purchases);
            assertTrue(obtainedPriceList.size() > 0);
            assertEquals(0, leftovers.size());
        });
    }

}