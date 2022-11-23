package ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl;

import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineObject;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;

public class RemoveMessageCommandDto extends SerializableInlineObject {

    private final Integer messageId;
    private final Long chatId;

    public RemoveMessageCommandDto(Integer messageId, Long chatId) {
        super(SerializableInlineType.REMOVE_BOT_MESSAGE);
        this.messageId = messageId;
        this.chatId = chatId;
    }

    public Integer getMessageId() {
        return this.messageId;
    }

    public Long getChatId() {
        return this.chatId;
    }
}
