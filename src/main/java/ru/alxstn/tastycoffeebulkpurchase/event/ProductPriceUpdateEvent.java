package ru.alxstn.tastycoffeebulkpurchase.event;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class ProductPriceUpdateEvent extends ProductUpdateEvent {
    public ProductPriceUpdateEvent(Object source, Product oldProduct, Product newProduct) {
        super(source, oldProduct, newProduct);
    }
}
