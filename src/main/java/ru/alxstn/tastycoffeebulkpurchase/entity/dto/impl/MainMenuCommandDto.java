package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class MainMenuCommandDto extends SerializableInlineObject {

    @JsonProperty("m")
    private String message;

    public MainMenuCommandDto() {
        super(SerializableInlineType.SET_MAIN_COMMAND);
    }

    public MainMenuCommandDto(String message) {
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
