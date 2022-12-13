package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class NewProductDiscoveredEvent extends ApplicationEvent {
    private final Product newProduct;

    public NewProductDiscoveredEvent(Object source, Product newProduct) {
        super(source);
        this.newProduct = newProduct;
    }

    public Product getNewProduct() {
        return newProduct;
    }
}
