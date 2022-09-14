package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import com.codeborne.selenide.SelenideElement;
import org.springframework.stereotype.Component;
import ru.alxstn.tastycoffeebulkpurchase.configuration.TastyCoffeeConfigProperties;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

@Component
public class TastyCoffeeLoginPage {
    private final TastyCoffeeConfigProperties tastyCoffeeConfig;
    private final SelenideElement bulkPurchaseClientLoginButton = $(byText("Вход для оптовых клиентов"));
    private final SelenideElement usernameInput = $(byName("login"));
    private final SelenideElement passwordInput = $(byName("password"));
    private final SelenideElement loginButton = $(byText("Войти"));

    public TastyCoffeeLoginPage(TastyCoffeeConfigProperties properties) {
        tastyCoffeeConfig = properties;
    }


    void login() {
        open(tastyCoffeeConfig.getUrl());
        bulkPurchaseClientLoginButton.click();
        usernameInput.setValue(tastyCoffeeConfig.getUserName());
        passwordInput.setValue(tastyCoffeeConfig.getPassword());
        loginButton.click();
    }
}