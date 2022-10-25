package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SavePurchaseCommandDto extends SerializableInlineObject {

    private long customerId;
    private Product product;
    private int productCount;
    private String productForm;

    public SavePurchaseCommandDto() {
        super(SerializableInlineType.ADD_PURCHASE);
    }

    public SavePurchaseCommandDto(long customerId, Product product, int productCount, String productForm) {
        this();
        this.customerId = customerId;
        this.product = product;
        this.productCount = productCount;
        this.productForm = productForm;
    }

}
