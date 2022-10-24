package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductCategoryCommandDto extends SerializableInlineObject {

    private String message;

    public SetProductCategoryCommandDto() {
        super(SerializableInlineType.SET_CATEGORY);
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
