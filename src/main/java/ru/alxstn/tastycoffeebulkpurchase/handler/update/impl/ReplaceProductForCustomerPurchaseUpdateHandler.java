package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.ReplaceProductForCustomerPurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
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
        return SerializableInlineType.REMOVE_PRODUCT_FOR_CUSTOMER;
    }

    @Override
    protected void handleCallback(Update update, ReplaceProductForCustomerPurchaseCommandDto dto) {
        Customer customer = customerRepository.getByChatId(update.getMessage().getChatId());
        Session session = sessionManagerService.getUnfinishedSession();

        purchaseManagerService.replacePurchaseProductForCustomerInSession(customer, session,
                dto.getOldProduct(), dto.getNewProduct());

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(0)
                .text("Заменено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        // ToDo: remove previous message with buttons <Remove>, <Replace>
    }
}
