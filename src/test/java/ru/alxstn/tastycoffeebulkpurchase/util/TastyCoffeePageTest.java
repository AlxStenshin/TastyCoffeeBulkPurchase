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
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(TastyCoffeeConfigProperties.class)
@TestPropertySource("classpath:application-test.properties")
@TestPropertySource("classpath:secrets-test.properties")
class TastyCoffeePageTest {

    Logger logger = LogManager.getLogger(TastyCoffeePageTest.class);

    private TastyCoffeePage webPage;

    @Autowired
    TastyCoffeeConfigProperties tastyCoffeeTestConfig;

    @Mock
    ApplicationEventPublisher publisher;

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
    void shouldCorrectlyObtainPriceListAndPlaceOrder() {
        // All functionality combined in one test because of parser high time-consuming behavior
        Assertions.assertDoesNotThrow(() -> {
            logger.info("Now Obtaining PriceList");
            List<Product> obtainedPriceList = webPage.buildPriceList();

            Customer c = new Customer();
            Session s = new Session();

            List<Purchase> purchases = obtainedPriceList.stream()
                    .map(product -> new Purchase(
                            c, product, s, product.isGrindable() ? "Мелкий" : "", 1))
                    .collect(Collectors.toList());

            List<Purchase> beansPurchases = obtainedPriceList.stream()
                    .filter(Product::isGrindable)
                    .map(product -> new Purchase(
                            c, product, s, "", 1))
                    .collect(Collectors.toList());

            purchases.addAll(beansPurchases);
            logger.info("Now Building Order");
            List<Purchase> leftovers = webPage.placeOrder(purchases);
            assertTrue(obtainedPriceList.size() > 0);
            assertEquals(0, leftovers.size());
        });
    }

}