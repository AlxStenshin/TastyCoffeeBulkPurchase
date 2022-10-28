package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

import java.util.List;

public class ClearPurchasesCommandDto extends SerializableInlineObject {

    private List<Purchase> purchaseList;

    public ClearPurchasesCommandDto() {
        super(SerializableInlineType.CLEAR_PURCHASES);
    }
    public ClearPurchasesCommandDto(List<Purchase> purchaseList) {
        this();
        this.purchaseList = purchaseList;
    }

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }
}
