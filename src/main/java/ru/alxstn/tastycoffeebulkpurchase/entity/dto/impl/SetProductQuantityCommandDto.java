package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductQuantityCommandDto extends SerializableInlineObject {

    private long productId;
    private int productQuantity;
    private String productForm;

    public SetProductQuantityCommandDto() {
        super(SerializableInlineType.EDIT_PRODUCT_QUANTITY);
    }

    public SetProductQuantityCommandDto(long productId, int productQuantity) {
        this();
        this.productId = productId;
        this.productQuantity = productQuantity;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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
