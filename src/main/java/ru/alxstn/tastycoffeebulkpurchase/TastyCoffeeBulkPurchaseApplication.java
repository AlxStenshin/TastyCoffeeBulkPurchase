package ru.alxstn.tastycoffeebulkpurchase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TelegramBotConfigProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({
        TastyCoffeeConfigProperties.class,
        TelegramBotConfigProperties.class})
public class TastyCoffeeBulkPurchaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TastyCoffeeBulkPurchaseApplication.class, args);
    }

}
