package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.serialize.DtoSerializer;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.SessionRepository;
import ru.alxstn.tastycoffeebulkpurchase.util.DateTimeProvider;


@Component
public class SavePurchaseUpdateHandler extends CallbackUpdateHandler<SavePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(SavePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final SessionRepository sessionRepository;
    private final CustomerRepository customerRepository;
    private final PurchaseRepository purchaseRepository;
    private final DateTimeProvider dateTimeProvider;

    public SavePurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                     CustomerRepository customerRepository,
                                     PurchaseRepository purchaseRepository,
                                     SessionRepository sessionRepository,
                                     DateTimeProvider dateTimeProvider) {
        this.publisher = publisher;
        this.sessionRepository = sessionRepository;
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
        this.dateTimeProvider = dateTimeProvider;
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
        logger.info("Save Purchase Command Received.");

        purchaseRepository.save(new Purchase(
                customerRepository.getReferenceById(dto.getCustomerId()),
                dto.getProduct(),
                sessionRepository.getCurrentSession(dateTimeProvider.getCurrentTimestamp()),
                dto.getProductForm(),
                dto.getProductCount()));

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(10)
                .text("Сохранено!")
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        // ToDo: Show Edit Purchase DTO After That
    }
}
