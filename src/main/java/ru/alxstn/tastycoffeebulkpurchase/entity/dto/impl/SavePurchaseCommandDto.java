package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SavePurchaseCommandDto extends SerializableInlineObject {

    private Customer customer;
    private Product product;
    private Session session;
    private int productCount;
    private String productForm;

    public SavePurchaseCommandDto() {
        super(SerializableInlineType.ADD_PURCHASE);
    }

    public SavePurchaseCommandDto(Customer customer, Product product, Session session, int productCount, String productForm) {
        this();
        this.customer = customer;
        this.product = product;
        this.session = session;
        this.productCount = productCount;
        this.productForm = productForm;
    }

    public Product getProduct() {
        return product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getProductCount() {
        return productCount;
    }

    public String getProductForm() {
        return productForm;
    }

    public Session getSession() {
        return session;
    }
}
