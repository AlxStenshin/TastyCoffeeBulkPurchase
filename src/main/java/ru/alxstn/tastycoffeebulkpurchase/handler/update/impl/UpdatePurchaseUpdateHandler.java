package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.UpdatePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

@Component
public class UpdatePurchaseUpdateHandler extends CallbackUpdateHandler<UpdatePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(SavePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;

    public UpdatePurchaseUpdateHandler(ApplicationEventPublisher publisher, PurchaseRepository purchaseRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    protected Class<UpdatePurchaseCommandDto> getDtoType() {
        return UpdatePurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.UPDATE_PURCHASE;
    }

    @Override
    protected void handleCallback(Update update, UpdatePurchaseCommandDto dto) {
        Purchase purchase = dto.getPurchase();
        logger.info("Update Purchase Command Received: " + purchase);

        purchaseRepository.updatePurchaseWithId(purchase.getId(), purchase.getCount(), purchase.getProductForm());

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Сохранено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        // ToDo: Show Edit Purchase List After That
    }
}
