package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.ClearPurchasesCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

import java.util.List;


@Component
public class ClearPurchasesUpdateHandler extends CallbackUpdateHandler<ClearPurchasesCommandDto> {

    Logger logger = LogManager.getLogger(ClearPurchasesUpdateHandler.class);
    private final PurchaseRepository purchaseRepository;
    private final ApplicationEventPublisher publisher;

    public ClearPurchasesUpdateHandler(PurchaseRepository purchaseRepository,
                                       ApplicationEventPublisher publisher) {
        this.purchaseRepository = purchaseRepository;
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
        purchaseRepository.deleteAll(purchaseList);

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Заказ очищен!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        // ToDo: Show Main Menu After That
    }
}