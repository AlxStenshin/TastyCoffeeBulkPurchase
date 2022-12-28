package ru.alxstn.tastycoffeebulkpurchase.entity;

public class PurchaseEntry {

    private final Product product;
    private final String productForm;
    private final int count;

    public PurchaseEntry(Product product, String productForm, int count) {
        this.product = product;
        this.productForm = productForm;
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public String getProductForm() {
        return productForm;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return getProduct().getShortName() +
                (getProductForm().isBlank() ? "" : ", " + getProductForm()) +
                " - " +
                getCount() + " шт.\n";
    }
}
