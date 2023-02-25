package ru.alxstn.tastycoffeebulkpurchase.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;

public class RequestEmojiLegendCommandDto extends SerializableInlineObject {

    public RequestEmojiLegendCommandDto() {
        super(SerializableInlineType.SHOW_EMOJI_LEGEND);
    }
}
