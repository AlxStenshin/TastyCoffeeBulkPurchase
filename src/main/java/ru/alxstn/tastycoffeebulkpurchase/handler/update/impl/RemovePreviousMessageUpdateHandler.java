package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RemoveMessageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;

@Component
public class RemovePreviousMessageUpdateHandler extends CallbackUpdateHandler<RemoveMessageCommandDto> {

    Logger logger = LogManager.getLogger(RemovePreviousMessageUpdateHandler.class);
    private final ApplicationEventPublisher publisher;

    public RemovePreviousMessageUpdateHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    protected Class<RemoveMessageCommandDto> getDtoType() {
        return RemoveMessageCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REMOVE_BOT_MESSAGE;
    }

    @Override
    protected void handleCallback(Update update, RemoveMessageCommandDto dto) {
        logger.info("Remove Message Command Received");

        publisher.publishEvent(new RemoveMessageEvent(this,
                DeleteMessage.builder()
                        .messageId(dto.getMessageId())
                        .chatId(dto.getChatId())
                        .build()));
    }
}
