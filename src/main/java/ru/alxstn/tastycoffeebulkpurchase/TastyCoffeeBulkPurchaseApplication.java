package ru.alxstn.tastycoffeebulkpurchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(TastyCoffeeConfigProperties.class)
public class TastyCoffeeBulkPurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TastyCoffeeBulkPurchaseApplication.class, args);
    }

}
