package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class ReplaceProductForCustomerPurchaseCommandDto extends SerializableInlineObject {
    private final Product oldProduct;
    private final Product newProduct;

    public ReplaceProductForCustomerPurchaseCommandDto(Product oldProduct, Product newProduct) {
        super(SerializableInlineType.REPLACE_PRODUCT_FOR_CUSTOMER);
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
