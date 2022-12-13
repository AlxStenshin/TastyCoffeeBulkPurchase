package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class RemoveProductFromCustomerPurchaseCommandDto extends SerializableInlineObject {
    private final Product oldProduct;

    public RemoveProductFromCustomerPurchaseCommandDto(Product oldProduct) {
        super(SerializableInlineType.REMOVE_PRODUCT_FOR_CUSTOMER);
        this.oldProduct = oldProduct;
    }

    public Product getOldProduct() {
        return oldProduct;
    }
}
