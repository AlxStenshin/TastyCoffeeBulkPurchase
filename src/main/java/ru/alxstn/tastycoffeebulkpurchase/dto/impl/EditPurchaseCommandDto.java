package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class EditPurchaseCommandDto extends SerializableInlineObject {

    private Purchase purchase;

    public EditPurchaseCommandDto() {
        super(SerializableInlineType.EDIT_PURCHASE);
    }

    public EditPurchaseCommandDto(Purchase purchase) {
        this();
        this.purchase = purchase;
    }

    public EditPurchaseCommandDto(Purchase purchase, SerializableInlineObject previous) {
        this();
        this.purchase = purchase;
        super.setPrevious(previous);
    }


    public Purchase getPurchase() {
        return purchase;
    }
}
