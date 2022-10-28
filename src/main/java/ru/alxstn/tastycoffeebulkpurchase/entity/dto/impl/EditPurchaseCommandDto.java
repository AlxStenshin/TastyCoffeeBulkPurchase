package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class EditPurchaseCommandDto extends SerializableInlineObject {

    private Purchase purchase;

    public EditPurchaseCommandDto() {
        super(SerializableInlineType.EDIT_PURCHASE);
    }

    public EditPurchaseCommandDto(Purchase purchase) {
        this();
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
