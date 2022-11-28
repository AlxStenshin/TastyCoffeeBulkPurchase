package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RequestCustomerPurchaseSummaryCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.UpdateMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.exception.SessionNotFoundException;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.CustomerSummaryCreatorService;
import ru.alxstn.tastycoffeebulkpurchase.service.SessionManagerService;

@Component
public class RequestCustomerPurchaseSummaryUpdateHandler extends CallbackUpdateHandler<RequestCustomerPurchaseSummaryCommandDto> {

    Logger logger = LogManager.getLogger(RequestCustomerPurchaseSummaryUpdateHandler.class);
    private final CustomerSummaryCreatorService customerSummaryService;
    private final SessionManagerService sessionManagerService;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher publisher;

    public RequestCustomerPurchaseSummaryUpdateHandler(CustomerSummaryCreatorService customerSummaryService,
                                                       SessionManagerService sessionManagerService,
                                                       CustomerRepository customerRepository,
                                                       ApplicationEventPublisher publisher) {
        this.customerSummaryService = customerSummaryService;
        this.sessionManagerService = sessionManagerService;
        this.customerRepository = customerRepository;
        this.publisher = publisher;
    }

    @Override
    protected Class<RequestCustomerPurchaseSummaryCommandDto> getDtoType() {
        return RequestCustomerPurchaseSummaryCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REQUEST_PURCHASE_SUMMARY;
    }

    @Override
    protected void handleCallback(Update update, RequestCustomerPurchaseSummaryCommandDto dto) {
        long customerID = update.getCallbackQuery().getMessage().getChatId();
        logger.info("Customer Purchase Summary Request Received: " + customerID);

        String message;
        try {
            message = customerSummaryService.buildCustomerSummary(
                    customerRepository.getByChatId(customerID),
                    dto.getSession());

        } catch (SessionNotFoundException e) {
            message = sessionManagerService.getActiveSessionNotFoundMessage();
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
