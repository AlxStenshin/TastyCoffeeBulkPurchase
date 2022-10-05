package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineStringMessageObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class MainMenuCommandDto extends SerializableInlineStringMessageObject {
    public MainMenuCommandDto(String message) {
        super(SerializableInlineType.SET_MAIN_COMMAND, message);
    }
}
