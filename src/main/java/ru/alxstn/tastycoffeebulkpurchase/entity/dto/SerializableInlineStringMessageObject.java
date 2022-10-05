package ru.alxstn.tastycoffeebulkpurchase.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SerializableInlineStringMessageObject extends SerializableInlineObject {

    @JsonProperty("m")
    private String message;

    public SerializableInlineStringMessageObject(SerializableInlineType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
