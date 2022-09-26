package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class ProductFoundEvent extends ApplicationEvent {
    private Product product;

    public ProductFoundEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }
}
