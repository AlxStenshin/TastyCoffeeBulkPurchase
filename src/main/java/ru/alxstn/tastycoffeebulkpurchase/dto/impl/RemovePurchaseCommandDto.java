package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class RemovePurchaseCommandDto extends SerializableInlineObject {

    private Purchase purchase;

    public RemovePurchaseCommandDto() {
        super(SerializableInlineType.REMOVE_PURCHASE);
    }

    public RemovePurchaseCommandDto(Purchase purchase) {
        this();
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
