package ru.alxstn.tastycoffeebulkpurchase.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.model.api.categories.CategoriesResponse;
import ru.alxstn.tastycoffeebulkpurchase.model.api.categories.CategoryData;
import ru.alxstn.tastycoffeebulkpurchase.model.api.login.LoginResponse;
import ru.alxstn.tastycoffeebulkpurchase.model.api.product.ProductData;
import ru.alxstn.tastycoffeebulkpurchase.model.api.product.ProductsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TastyCoffeeApi {

    private final WebClient.Builder webClientBuilder;
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;

    public TastyCoffeeApi(WebClient.Builder webClientBuilder, TastyCoffeeConfigProperties tastyCoffeeConfig) {
        this.webClientBuilder = webClientBuilder;
        this.tastyCoffeeConfig = tastyCoffeeConfig;
    }

    public List<Product> buildPriceList() throws JsonProcessingException {
        var token = login();
        var categories = categoriesRequest(token);
        var products = productsRequest(token);

        return ProductMapper.map(categories, products);
    }

    public List<Product> buildOutputProducts(CategoriesResponse categories, ProductsResponse products) {
        var productToCategoryMap = new HashMap<CategoryData, List<ProductData>>();
        return List.of();
    }

    public String login() throws JsonProcessingException {
        String apiUrl = "https://api.tastycoffee.ru/api/v1/auth/login";

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("login", tastyCoffeeConfig.getUserName());
        bodyMap.put("password", tastyCoffeeConfig.getPassword());

        var response = webClientBuilder.build()
                .post()
                .uri(apiUrl)
                .body(BodyInserters.fromValue(bodyMap))
                .retrieve()
                .bodyToMono(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getValue(response), LoginResponse.class).getData().getAccessToken();
    }

    public CategoriesResponse categoriesRequest(String token) throws JsonProcessingException {
        String apiUrl = "https://api.tastycoffee.ru/api/v1/catalog/categories";

        var response = webClientBuilder.build()
                .get()
                .uri(apiUrl)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getValue(response), CategoriesResponse.class);
    }

    public ProductsResponse productsRequest(String token) throws JsonProcessingException {
        String apiUrl = "https://api.tastycoffee.ru/api/v1/catalog/products?sort=name-asc";

        var response = webClientBuilder.build()
                .get()
                .uri(apiUrl)
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getValue(response), ProductsResponse.class);
    }

    private String getValue(Mono<String> mono) {
        return mono.block();
    }
}