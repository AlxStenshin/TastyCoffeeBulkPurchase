package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RequestSessionSummaryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.session.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionSummaryCreatorService;

@Component
public class RequestSessionSummaryUpdateHandler extends CallbackUpdateHandler<RequestSessionSummaryCommandDto> {

    Logger logger = LogManager.getLogger(RequestSessionSummaryUpdateHandler.class);
    private final SessionSummaryCreatorService sessionSummaryCreatorService;
    private final ApplicationEventPublisher publisher;

    public RequestSessionSummaryUpdateHandler(SessionSummaryCreatorService sessionSummaryCreatorService,
                                              ApplicationEventPublisher publisher) {
        this.sessionSummaryCreatorService = sessionSummaryCreatorService;
        this.publisher = publisher;
    }

    @Override
    protected Class<RequestSessionSummaryCommandDto> getDtoType() {
        return RequestSessionSummaryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REQUEST_SESSION_SUMMARY;
    }

    @Override
    protected void handleCallback(Update update, RequestSessionSummaryCommandDto dto) {
        long customerID = update.getCallbackQuery().getMessage().getChatId();
        logger.info("Customer Session Summary Request Received: " + customerID);

        String message;
        try {
            message = sessionSummaryCreatorService.createSessionSummary(dto.getSession());
        } catch (SessionNotFoundException e) {
            message = e.getMessage();
        }

        publisher.publishEvent(new UpdateMessageEvent(this,
                EditMessageText.builder()
                        .text(message)
                        .parseMode("html")
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(customerID)
                        .build()));
    }
}
