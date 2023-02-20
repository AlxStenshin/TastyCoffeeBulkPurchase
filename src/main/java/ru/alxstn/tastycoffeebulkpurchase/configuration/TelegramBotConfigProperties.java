package ru.alxstn.tastycoffeebulkpurchase.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegram-bot")
// ToDo: add validation
public class TelegramBotConfigProperties {

    String name;
    String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
