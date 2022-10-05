package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineStringMessageObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetProductCommandDto extends SerializableInlineStringMessageObject {
    public SetProductCommandDto(String message) {
        super(SerializableInlineType.ADD_PRODUCT, message);
    }
}