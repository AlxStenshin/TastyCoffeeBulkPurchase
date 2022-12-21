package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

import java.util.List;

public class ClearPurchasesCommandDto extends SerializableInlineObject {
    private Customer customer;
    private List<Purchase> purchaseList;

    public ClearPurchasesCommandDto() {
        super(SerializableInlineType.CLEAR_PURCHASES);
    }
    public ClearPurchasesCommandDto(Customer customer, List<Purchase> purchaseList) {
        this();
        this.customer = customer;
        this.purchaseList = purchaseList;
    }

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }

    public Customer getCustomer() {
        return customer;
    }
}
