package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Payment;
import ru.alxstn.tastycoffeebulkpurchase.entity.Purchase;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.UpdatePurchaseCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.event.AlertMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.CustomerSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.event.SessionSummaryCheckRequestEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;

@Component
public class UpdatePurchaseUpdateHandler extends CallbackUpdateHandler<UpdatePurchaseCommandDto> {

    Logger logger = LogManager.getLogger(UpdatePurchaseUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseManagerService purchaseManagerService;
    private final PaymentManagerService paymentManagerService;

    public UpdatePurchaseUpdateHandler(ApplicationEventPublisher publisher,
                                       PurchaseManagerService purchaseManagerService,
                                       PaymentManagerService paymentManagerService) {
        this.publisher = publisher;
        this.purchaseManagerService = purchaseManagerService;
        this.paymentManagerService = paymentManagerService;
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
        Purchase purchase = dto.getPurchase();
        try {
            purchaseManagerService.save(purchase);
            Payment payment = paymentManagerService.getCustomerSessionPayment(
                            purchase.getSession(), purchase.getCustomer())
                    .orElse(new Payment(purchase.getCustomer(), purchase.getSession()));
            paymentManagerService.save(payment);
        } catch (Exception e) {
            logger.error("Saving purchase : " + purchase + " " + e.getMessage());
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
