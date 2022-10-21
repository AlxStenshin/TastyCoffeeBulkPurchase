package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SavePurchaseCommandDto extends SerializableInlineObject {

    private long customerId;
    private long productId;
    private int productCount;
    private String productForm;

    public SavePurchaseCommandDto() {
        super(SerializableInlineType.ADD_PURCHASE);
    }

    public SavePurchaseCommandDto(long customerId, long productId, int productCount, String productForm) {
        this();
        this.customerId = customerId;
        this.productId = productId;
        this.productCount = productCount;
        this.productForm = productForm;
    }

}
