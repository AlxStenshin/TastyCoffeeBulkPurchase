package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class PriceListWebScrapperService {

    private final TastyCoffeePage tastyCoffeeMain;

    public PriceListWebScrapperService(TastyCoffeePage loginPage) {
        this.tastyCoffeeMain = loginPage;
    }

    @PostConstruct
    void doIt() {
        getPrices();
    }

    private void getPrices() {
        tastyCoffeeMain.login();
        List<Product> priceList = tastyCoffeeMain.buildPriceList();
        System.out.println("Got " + priceList.size() + " items in price list");
    }

}
