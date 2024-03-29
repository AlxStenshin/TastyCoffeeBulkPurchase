package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.RemoveMessageCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;

@Component
public class RemoveMessageUpdateHandler extends CallbackUpdateHandler<RemoveMessageCommandDto> {

    Logger logger = LogManager.getLogger(RemoveMessageUpdateHandler.class);
    private final ApplicationEventPublisher publisher;

    public RemoveMessageUpdateHandler(ApplicationEventPublisher publisher) {
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
