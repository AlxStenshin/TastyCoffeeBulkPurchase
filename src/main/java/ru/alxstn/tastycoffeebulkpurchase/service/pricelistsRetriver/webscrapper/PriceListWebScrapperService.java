package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PriceListWebScrapperService {

    private final TastyCoffeeLoginPage loginPage;

    public PriceListWebScrapperService(TastyCoffeeLoginPage loginPage) {
        this.loginPage = loginPage;
    }

    @PostConstruct
    void doIt() {
        callAuthDialog();
    }
    public void callAuthDialog() {
        loginPage.login();
    }

}
