package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.ReplaceProductForCustomerPurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

@Component
public class ReplaceProductForCustomerPurchaseUpdateHandler
        extends CallbackUpdateHandler<ReplaceProductForCustomerPurchaseCommandDto> {

    private final ApplicationEventPublisher publisher;
    private final SessionManagerService sessionManagerService;
    private final PurchaseManagerService purchaseManagerService;
    private final CustomerRepository customerRepository;

    public ReplaceProductForCustomerPurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                                          SessionManagerService sessionManagerService,
                                                          PurchaseManagerService purchaseManagerService,
                                                          CustomerRepository customerRepository) {
        this.publisher = publisher;
        this.sessionManagerService = sessionManagerService;
        this.purchaseManagerService = purchaseManagerService;
        this.customerRepository = customerRepository;
    }


    @Override
    protected Class<ReplaceProductForCustomerPurchaseCommandDto> getDtoType() {
        return ReplaceProductForCustomerPurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REPLACE_PRODUCT_FOR_CUSTOMER;
    }

    @Override
    protected void handleCallback(Update update, ReplaceProductForCustomerPurchaseCommandDto dto) {
        Customer customer = customerRepository.getByChatId(update.getCallbackQuery().getMessage().getChatId());
        Session session = sessionManagerService.getUnfinishedSession();

        purchaseManagerService.replacePurchaseProductForCustomerInSession(customer, session,
                dto.getOldProduct(), dto.getNewProduct());

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(0)
                .text("Обновлено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        publisher.publishEvent(new RemoveMessageEvent(this,
                DeleteMessage.builder()
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .build()));
    }
}
