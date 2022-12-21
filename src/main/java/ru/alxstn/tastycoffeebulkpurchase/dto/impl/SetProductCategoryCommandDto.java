package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class SetProductCategoryCommandDto extends SerializableInlineObject {

    private String message;

    public SetProductCategoryCommandDto() {
        super(SerializableInlineType.SET_PRODUCT_CATEGORY);
    }

    public SetProductCategoryCommandDto(String message, SerializableInlineObject previous) {
        this();
        this.message = message;
        this.setPrevious(previous);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
