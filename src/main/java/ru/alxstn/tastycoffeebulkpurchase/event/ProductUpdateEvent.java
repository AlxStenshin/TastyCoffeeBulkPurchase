package ru.alxstn.tastycoffeebulkpurchase.event;

import org.springframework.context.ApplicationEvent;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class ProductUpdateEvent extends ApplicationEvent {
    private final Product oldProduct;
    private final Product newProduct;

    public ProductUpdateEvent(Object source, Product oldProduct, Product newProduct) {
        super(source);
        this.oldProduct = oldProduct;
        this.newProduct = newProduct;
    }

    public Product getOldProduct() {
        return oldProduct;
    }

    public Product getNewProduct() {
        return newProduct;
    }
}
