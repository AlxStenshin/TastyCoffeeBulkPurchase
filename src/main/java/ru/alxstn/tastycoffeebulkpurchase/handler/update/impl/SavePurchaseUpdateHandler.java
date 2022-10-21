package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;


@Component
public class SavePurchaseUpdateHandler extends CallbackUpdateHandler<SavePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(SavePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final DtoSerializer serializer;

    public SavePurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                     PurchaseRepository purchaseRepository,
                                     DtoSerializer serializer) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.serializer = serializer;
    }

    @Override
    protected Class<SavePurchaseCommandDto> getDtoType() {
        return SavePurchaseCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.ADD_PURCHASE;
    }

    @Override
    protected void handleCallback(Update update, SavePurchaseCommandDto dto) {

    publisher.publishEvent(new AlertMessageEvent( this, AnswerCallbackQuery.builder()
                        .cacheTime(10)
                        .text("Сохранено!")
                        .showAlert(false)
                        .callbackQueryId(update.getCallbackQuery().getId())
                        .build()));
    }
}
