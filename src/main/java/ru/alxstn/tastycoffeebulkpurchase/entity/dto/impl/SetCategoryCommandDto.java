package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineStringMessageObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class SetCategoryCommandDto extends SerializableInlineStringMessageObject {
        public SetCategoryCommandDto(String message) {
        super(SerializableInlineType.SET_CATEGORY, message);
    }
}
