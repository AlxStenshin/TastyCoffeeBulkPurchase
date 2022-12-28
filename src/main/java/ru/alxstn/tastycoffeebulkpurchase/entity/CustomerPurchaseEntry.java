package ru.alxstn.tastycoffeebulkpurchase.entity;

public class CustomerPurchaseEntry {
    private final Customer customer;
    private final PurchaseEntry purchaseEntry;

    public CustomerPurchaseEntry(Customer customer, PurchaseEntry purchaseEntry) {
        this.customer = customer;
        this.purchaseEntry = purchaseEntry;
    }

    public Customer getCustomer() {
        return customer;
    }

    public PurchaseEntry getPurchaseEntry() {
        return purchaseEntry;
    }
}
