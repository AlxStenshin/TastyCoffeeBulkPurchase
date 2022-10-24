package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductSubCategoryCommandDto extends SerializableInlineObject {

    private String message;

    public SetProductSubCategoryCommandDto() {
        super(SerializableInlineType.SET_SUBCATEGORY);
    }

    public SetProductSubCategoryCommandDto(String message, SerializableInlineObject previous) {
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
