package ru.alxstn.tastycoffeebulkpurchase.event;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;

public class ProductSpecialMarkUpdateEvent extends ProductUpdateEvent {

    public ProductSpecialMarkUpdateEvent(Object source, Product oldProduct, Product newProduct) {
        super(source, oldProduct, newProduct);
    }
}
