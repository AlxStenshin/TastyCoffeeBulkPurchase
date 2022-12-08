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
import ru.alxstn.tastycoffeebulkpurchase.entity.dto.impl.UpdatePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.CustomerSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.PaymentRepository;
import ru.alxstn.tastycoffeebulkpurchase.repository.PurchaseRepository;

@Component
public class UpdatePurchaseUpdateHandler extends CallbackUpdateHandler<UpdatePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(UpdatePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseRepository purchaseRepository;
    private final PaymentRepository paymentRepository;

    public UpdatePurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                       PurchaseRepository purchaseRepository,
                                       PaymentRepository paymentRepository) {
        this.publisher = publisher;
        this.purchaseRepository = purchaseRepository;
        this.paymentRepository = paymentRepository;
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
        logger.info("Update Purchase Command Received");
        Purchase newPurchase = dto.getPurchase();
        Purchase purchase = purchaseRepository.getPurchaseIgnoringProductQuantity(
                        newPurchase.getCustomer(),
                        newPurchase.getProduct(),
                        newPurchase.getSession(),
                        newPurchase.getProductForm())
                .orElse(new Purchase(newPurchase.getCustomer(),
                        newPurchase.getProduct(),
                        newPurchase.getSession(),
                        newPurchase.getProductForm(),
                        0));

        int previousCount = purchase.getCount();
        int newCount = newPurchase.getCount();
        purchase.setCount(previousCount == 0 ? newCount : previousCount + newCount);
        try {
            purchaseRepository.save(purchase);
            Payment payment = paymentRepository.getCustomerSessionPayment(
                    purchase.getSession(), purchase.getCustomer())
                    .orElse(new Payment(purchase.getCustomer(), purchase.getSession()));
            paymentRepository.save(payment);
        } catch (Exception e) {
            logger.error("Saving new purchase : " + purchase + " " + e.getMessage());
        }

        publisher.publishEvent(new AlertMessageEvent(this, AnswerCallbackQuery.builder()
                .cacheTime(0)
                .text("Сохранено!\n" + purchase.getProduct().getDisplayName())
                .showAlert(false)
                .callbackQueryId(update.getCallbackQuery().getId())
                .build()));

        publisher.publishEvent(new CustomerSummaryCheckRequestEvent(this, purchase.getCustomer(), "Save"));
        publisher.publishEvent(new SessionSummaryCheckRequestEvent(this, "Save"));

        // ToDo: Show Edit Purchase List After That
    }
}
