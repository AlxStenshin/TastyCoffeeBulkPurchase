package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class ShowProductNotAvailableAlertDto extends SerializableInlineObject {

    private String productName;

    public ShowProductNotAvailableAlertDto() {
        super(SerializableInlineType.SHOW_PRODUCT_UNAVAILABLE_ALERT);
    }

    public ShowProductNotAvailableAlertDto(String productName) {
        this();
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
