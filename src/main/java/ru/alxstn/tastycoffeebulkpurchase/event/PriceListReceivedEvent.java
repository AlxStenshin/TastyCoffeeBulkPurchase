package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

import java.util.List;

public class PriceListReceivedEvent extends ApplicationEvent {

    private final List<Product> priceList;

    public PriceListReceivedEvent(Object source, List<Product> priceList) {
        super(source);
        this.priceList = priceList;
    }

    public List<Product> getPriceList() {
        return priceList;
    }

}
