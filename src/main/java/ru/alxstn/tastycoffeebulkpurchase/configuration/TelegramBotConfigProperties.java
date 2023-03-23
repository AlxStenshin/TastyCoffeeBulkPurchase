package ru.alxstn.tastycoffeebulkpurchase.configuration;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Validated
@ConfigurationProperties("telegram-bot")
public class TelegramBotConfigProperties {

    @NotBlank
    String name;

    @NotBlank
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
