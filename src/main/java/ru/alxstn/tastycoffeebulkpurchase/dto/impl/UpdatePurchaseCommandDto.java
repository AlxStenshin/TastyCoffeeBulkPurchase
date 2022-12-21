package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class UpdatePurchaseCommandDto extends SerializableInlineObject {

    private Purchase purchase;

    public UpdatePurchaseCommandDto() {
        super(SerializableInlineType.UPDATE_PURCHASE);
    }

    public UpdatePurchaseCommandDto(Purchase purchase) {
        this();
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
