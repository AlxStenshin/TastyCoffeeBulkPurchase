package ru.alxstn.tastycoffeebulkpurchase.service.pricelistsRetriver.webscrapper;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.event.ProductFoundEvent;
import ru.alxstn.tastycoffeebulkpurchase.repository.PriceListRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.pricelistsSaver.PriceListFileSaver;

import java.util.List;

@Service
public class PriceListWebScrapperService {

    private final TastyCoffeePage tastyCoffeeWebPage;
    private final PriceListFileSaver priceListSaver;
    private final PriceListRepository repository;


    public PriceListWebScrapperService(TastyCoffeePage page,
                                       PriceListRepository repository,
                                       PriceListFileSaver priceListSaver) {
        this.tastyCoffeeWebPage = page;
        this.priceListSaver = priceListSaver;
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getPrices() {
        tastyCoffeeWebPage.login();
        List<Product> priceList = tastyCoffeeWebPage.buildPriceList();
        System.out.println("Got " + priceList.size() + " items in price list");
        priceListSaver.save(priceList, "priceList.json");
    }

    @EventListener
    public void handleNewProduct(final ProductFoundEvent event) {
        Product product = event.getProduct();
        System.out.println("Trying to save new product: " + product);
        repository.save(product);
    }
}
