package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class PlaceOrderCommandDto extends SerializableInlineObject {

    private String message;

    public PlaceOrderCommandDto() {
        super(SerializableInlineType.SET_MAIN_COMMAND);
    }

    public PlaceOrderCommandDto(String message) {
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
