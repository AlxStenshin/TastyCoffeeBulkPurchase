package ru.alxstn.tastycoffeebulkpurchase.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
@TestPropertySource("classpath:secrets.properties")
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
    void shouldCorrectlyAddBerryToPurchaseList() {
        Product berry = new Product("Бэрри", new BigDecimal(1),
                "",
                new ProductPackage("Упаковка 250 г"),
                "КОФЕ ДЛЯ МОЛОЧНЫХ НАПИТКОВ",
                "Смеси для молочных напитков",
                "",
                true);

        Map<Product, Integer> purchases = new HashMap<>();
        purchases.put(berry, 11);

        Assertions.assertDoesNotThrow(() -> {
            List<Product> leftovers = webPage.placeOrder(purchases);
            assertEquals(0, leftovers.size());
        });
    }

    @Disabled
    @Test
    void shouldCorrectlyObtainPriceListAndPlaceOrder() {
        // All functionality combined in one test because of parser high time-consuming behavior
        Assertions.assertDoesNotThrow(() -> {
            logger.info("Now Obtaining PriceList");
            List<Product> obtainedPriceList = webPage.buildPriceList();


            Map<Product, Integer> purchases = new HashMap<>();
            obtainedPriceList.forEach(p -> purchases.put(p, 5));

            logger.info("Now Building Order");
            List<Product> leftovers = webPage.placeOrder(purchases);
            assertTrue(obtainedPriceList.size() > 0);
            assertEquals(0, leftovers.size());
        });
    }

}