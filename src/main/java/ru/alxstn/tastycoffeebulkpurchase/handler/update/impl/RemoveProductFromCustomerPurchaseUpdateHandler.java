package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.RemoveProductFromCustomerPurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

@Component
public class RemoveProductFromCustomerPurchaseUpdateHandler
        extends CallbackUpdateHandler<RemoveProductFromCustomerPurchaseCommandDto> {

    private final ApplicationEventPublisher publisher;
    private final SessionManagerService sessionManagerService;
    private final PurchaseManagerService purchaseManagerService;
    private final CustomerRepository customerRepository;

    public RemoveProductFromCustomerPurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                                          SessionManagerService sessionManagerService,
                                                          PurchaseManagerService purchaseRepository,
                                                          CustomerRepository customerRepository) {
        this.publisher = publisher;
        this.sessionManagerService = sessionManagerService;
        this.purchaseManagerService = purchaseRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected Class<RemoveProductFromCustomerPurchaseCommandDto> getDtoType() {
        return RemoveProductFromCustomerPurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REMOVE_PRODUCT_FOR_CUSTOMER;
    }

    @Override
    protected void handleCallback(Update update, RemoveProductFromCustomerPurchaseCommandDto dto) {
        Customer customer = customerRepository.getByChatId(update.getCallbackQuery().getMessage().getChatId());
        Session session = sessionManagerService.getUnfinishedSession();

        purchaseManagerService.removePurchaseForCustomerWithProductInSession(customer, session, dto.getOldProduct());

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(0)
                .text("Удалено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        publisher.publishEvent(new RemoveMessageEvent(this,
                DeleteMessage.builder()
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .build()));

        // ToDo: remove previous message with buttons <Remove>, <Replace>
    }

}
