package ru.alxstn.tastycoffeebulkpurchase.handler.update.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alxstn.tastycoffeebulkpurchase.entity.Customer;
import ru.alxstn.tastycoffeebulkpurchase.entity.Session;
import ru.alxstn.tastycoffeebulkpurchase.dto.SerializableInlineType;
import ru.alxstn.tastycoffeebulkpurchase.dto.impl.SetOrderPaidCommandDto;
import ru.alxstn.tastycoffeebulkpurchase.dto.serialize.DtoDeserializer;
import ru.alxstn.tastycoffeebulkpurchase.event.bot.SendMessageEvent;
import ru.alxstn.tastycoffeebulkpurchase.handler.update.CallbackUpdateHandler;
import ru.alxstn.tastycoffeebulkpurchase.repository.CustomerRepository;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PaymentManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.PurchaseManagerService;
import ru.alxstn.tastycoffeebulkpurchase.service.repositoryManager.SessionManagerService;

@Component
public class SetOrderPaidStatusUpdateHandler extends CallbackUpdateHandler<SetOrderPaidCommandDto> {

    Logger logger = LogManager.getLogger(SetOrderPaidStatusUpdateHandler.class);
    private final ApplicationEventPublisher publisher;
    private final PurchaseManagerService purchaseManagerService;
    private final PaymentManagerService paymentManagerService;
    private final CustomerRepository customerRepository;
    private final SessionManagerService sessionManagerService;

    @Autowired
    private DtoDeserializer deserializer;

    public SetOrderPaidStatusUpdateHandler(ApplicationEventPublisher publisher,
                                           PurchaseManagerService purchaseManagerService,
                                           CustomerRepository customerRepository,
                                           PaymentManagerService paymentManagerService,
                                           SessionManagerService sessionManagerService) {
        this.publisher = publisher;
        this.purchaseManagerService = purchaseManagerService;
        this.customerRepository = customerRepository;
        this.paymentManagerService = paymentManagerService;
        this.sessionManagerService = sessionManagerService;
    }

    @Override
    protected Class<SetOrderPaidCommandDto> getDtoType() {
        return SetOrderPaidCommandDto.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.PAYMENT_CONFIRMATION;
    }

    @Override
    protected void handleCallback(Update update, SetOrderPaidCommandDto dto) {
        Session currentSession = dto.getSession();
        Long eventEmitterId = update.getCallbackQuery().getMessage().getChatId();

        logger.info("Command received: Payment Confirmation from user " +
                eventEmitterId + ", session Id: " + currentSession.getId());

        Customer eventEmitter = customerRepository.getByChatId(eventEmitterId);
        paymentManagerService.registerPayment(currentSession, eventEmitter);
        int paidOrders = paymentManagerService.getCompletePaymentsCount(currentSession).orElse(0);
        int totalOrders = paymentManagerService.getSessionCustomersCount(currentSession).orElse(0);

        for (Customer c : purchaseManagerService.getSessionCustomers(currentSession)) {
            if (c.getNotificationSettings().isReceivePaymentConfirmationNotification()) {
                publisher.publishEvent(new SendMessageEvent(this, SendMessage.builder()
                        .text("Пользователь " + eventEmitter + " оплатил свой заказ.\n" +
                                "Оплачено заказов: " + paidOrders +
                                " из " + totalOrders)
                        .chatId(c.getChatId().toString())
                        .build()));
            }
        }
        if (paidOrders == totalOrders) {
            currentSession.setFinished(true);
            sessionManagerService.saveSession(currentSession);
            // ToDo: Emit All Orders Paid Event Message
        }
    }

}
