package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetCategoryCommandDto extends SerializableInlineObject {

    @JsonProperty("m")
    private String message;

    public SetCategoryCommandDto() {
        super(SerializableInlineType.SET_CATEGORY);
    }

    public SetCategoryCommandDto(String message) {
        this();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
