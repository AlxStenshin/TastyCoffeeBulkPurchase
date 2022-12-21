package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.ClearPurchasesCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.CustomerSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.RemoveMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

import java.util.List;


@Component
public class ClearPurchasesUpdateHandler extends CallbackUpdateHandler<ClearPurchasesCommandDto> {

    Logger logger = LogManager.getLogger(ClearPurchasesUpdateHandler.class);
    private final PurchaseManagerService purchaseManagerService;
    private final ApplicationEventPublisher publisher;

    public ClearPurchasesUpdateHandler(PurchaseManagerService purchaseRepository,
                                       ApplicationEventPublisher publisher) {
        this.purchaseManagerService = purchaseRepository;
        this.publisher = publisher;
    }

    @Override
    protected Class<ClearPurchasesCommandDto> getDtoType() {
        return ClearPurchasesCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.CLEAR_PURCHASES;
    }

    @Override
    protected void handleCallback(Update update, ClearPurchasesCommandDto dto) {
        List<Purchase> purchaseList = dto.getPurchaseList();

        logger.info("Clear Purchases Command Received");
        purchaseManagerService.deleteAll(purchaseList);

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Заказ очищен!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        publisher.publishEvent(new RemoveMessageEvent(this,
                DeleteMessage.builder()
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .chatId(update.getCallbackQuery().getMessage().getChatId())
                        .build()));

        publisher.publishEvent(new CustomerSummaryCheckRequestEvent(this, dto.getCustomer(), "Clear"));
        publisher.publishEvent(new SessionSummaryCheckRequestEvent(this, "Clear"));
    }
}
