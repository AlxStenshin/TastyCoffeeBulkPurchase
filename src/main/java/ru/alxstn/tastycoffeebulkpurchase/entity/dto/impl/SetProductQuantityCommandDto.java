package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.Product;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductQuantityCommandDto extends SerializableInlineObject {

    private Product targetProduct;
    private int productQuantity;
    private String productForm = "";

    public SetProductQuantityCommandDto() {
        super(SerializableInlineType.EDIT_PRODUCT_QUANTITY);
    }

    public SetProductQuantityCommandDto(Product product, int productQuantity, SerializableInlineObject previous) {
        this();
        this.targetProduct = product;
        this.productQuantity = productQuantity;
        this.setPrevious(previous);
    }

    public SetProductQuantityCommandDto(SetProductQuantityCommandDto dto, int productQuantity) {
        this();
        this.targetProduct = dto.getTargetProduct();
        this.productForm = dto.getProductForm();
        this.productQuantity = productQuantity;
        this.setPrevious(dto.getPrevious());
    }

    public SetProductQuantityCommandDto(SetProductQuantityCommandDto dto, String form) {
        this();
        this.targetProduct = dto.getTargetProduct();
        this.productQuantity = dto.getProductQuantity();
        this.productForm = form;
        this.setPrevious(dto.getPrevious());
    }

    public Product getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(Product targetProduct) {
        this.targetProduct = targetProduct;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm(String productForm) {
        this.productForm = productForm;
    }
}
