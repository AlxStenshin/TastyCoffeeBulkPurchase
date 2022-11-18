package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.SavePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.DiscountCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PaymentRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;


@Component
public class SavePurchaseUpdateHandler extends CallbackUpdateHandler<SavePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(SavePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;

    public SavePurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                     PurchaseRepository purchaseRepository,
                                     PaymentRepository paymentRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
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

        Purchase purchase = new Purchase(
                dto.getCustomer(),
                dto.getProduct(),
                dto.getSession(),
                dto.getProductForm(),
                dto.getProductCount());

        try {
            purchaseRepository.save(purchase);
            if (paymentRepository.paymentRegistered(dto.getSession(), dto.getCustomer()).isEmpty()) {
                Payment payment = new Payment(dto.getCustomer(), dto.getSession());
                paymentRepository.save(payment);
            }

            publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                    .cacheTime(10)
                    .text("Сохранено!")
                    .showAlert(false)
                    .callbackQueryId(update.getCallbackQuery().getId())
                    .build()));

            publisher.publishEvent(new DiscountCheckRequestEvent(this, "Save"));
        } catch (Exception e) {
            logger.error("Saving new purchase : " + purchase + " " + e.getMessage());
        }
        // ToDo: Show Edit Purchase DTO After That
    }
}
