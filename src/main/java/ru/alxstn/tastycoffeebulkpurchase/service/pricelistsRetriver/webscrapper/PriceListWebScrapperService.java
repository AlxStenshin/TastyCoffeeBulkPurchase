package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PriceListWebScrapperService {

    private final TastyCoffeePage tastyCoffeeMain;

    public PriceListWebScrapperService(TastyCoffeePage loginPage) {
        this.tastyCoffeeMain = loginPage;
    }

    @PostConstruct
    void doIt() {
        callAuthDialog();
    }

    public void callAuthDialog() {
        tastyCoffeeMain.login();
        tastyCoffeeMain.expandAll();
    }

}
