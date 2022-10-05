package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineStringMessageObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetSubCategoryCommandDto extends SerializableInlineStringMessageObject {

    public SetSubCategoryCommandDto(String message) {
        super(SerializableInlineType.SET_SUBCATEGORY, message);
    }
}
