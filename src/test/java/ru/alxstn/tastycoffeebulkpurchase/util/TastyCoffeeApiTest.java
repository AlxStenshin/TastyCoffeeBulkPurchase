package ru.alxstn.tastycoffeebulkpurchase.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;

import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)

class TastyCoffeeApiTest {

    private TastyCoffeeApi api;

    @Mock
    TastyCoffeeConfigProperties tastyCoffeeTestConfig;

    @BeforeEach
    public void init() {
        api = new TastyCoffeeApi(tastyCoffeeTestConfig);

        when(tastyCoffeeTestConfig.getUserName()).thenReturn("alexander.stn@gmail.com");
        when(tastyCoffeeTestConfig.getPassword()).thenReturn("");
    }

    @Test
    void login_shouldReturnToken() throws JsonProcessingException {
        var token = api.login();
        Assertions.assertNotNull(token);
    }

    @Test
    void productsRequest_shouldReturnProductsList() throws JsonProcessingException {
        var token = api.login();
        var products = api.productsRequest(token);
        Assertions.assertNotNull(products);
    }

    @Test
    void categoriesRequest_shouldCategoriesList() throws JsonProcessingException {
        var token = api.login();
        var categories = api.categoriesRequest(token);
        Assertions.assertNotNull(categories);
    }

    @Test
    void mapping_shouldSucceed() throws JsonProcessingException {
        var token = api.login();
        var categories = api.categoriesRequest(token);
        var products = api.productsRequest(token);

        var result = ProductMapper.map(categories, products);
        Assertions.assertNotNull(result);
        System.out.println(result);
        System.out.println();
    }
}