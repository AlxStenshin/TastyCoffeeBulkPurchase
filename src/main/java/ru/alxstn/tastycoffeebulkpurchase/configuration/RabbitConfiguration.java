package ru.alxstn.tastycoffeebulkpurchase.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public Queue botUpdateQueue() {
        return new Queue("botUpdateQueue");
    }

}
