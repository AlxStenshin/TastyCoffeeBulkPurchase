package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.RemovePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

@Component
public class RemovePurchaseUpdateHandler extends CallbackUpdateHandler<RemovePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(RemovePurchaseUpdateHandler.class);
    private final PurchaseRepository purchaseRepository;
    private final ApplicationEventPublisher publisher;

    public RemovePurchaseUpdateHandler(PurchaseRepository purchaseRepository,
                                       ApplicationEventPublisher publisher) {
        this.purchaseRepository = purchaseRepository;
        this.publisher = publisher;
    }

    @Override
    protected Class<RemovePurchaseCommandDto> getDtoType() {
        return RemovePurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.REMOVE_PURCHASE;
    }

    @Override
    protected void handleCallback(Update update, RemovePurchaseCommandDto dto) {
        Purchase purchase = dto.getPurchase();
        logger.info("Remove Purchase Command Received: " + purchase);

        purchaseRepository.delete(purchase);
        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Удалено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        publisher.publishEvent(new SessionSummaryCheckRequestEvent(this, "Remove"));

        // ToDo: Show Edit Purchase List After That
    }
}
